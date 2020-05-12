package com.nieyue;

import bt.Bt;
import bt.StandaloneClientBuilder;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.Torrent;
import bt.net.InetPeerAddress;
import bt.runtime.Config;
import com.google.inject.Module;
import javafx.util.Callback;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

/**
 * bt客户端
 */
public class BtClient {
    /**
     * 开启
     * @param clientPort 客户端端口
     * @param serverPort 服务端端口
     * @param torrentFilePath 种子文件路径
     * @param fileDirPath 文件目录路径
     * @param isStopWhenDownloaded 下载完是否停止 ，true停止,false不停止
     * @param callback 回调
     */
    public static void start(int clientPort,int serverPort,boolean isStopWhenDownloaded,String torrentFilePath, String fileDirPath, Callback callback){
        // enable multithreaded verification of torrent data
        Config config = new Config() {
            @Override
            public int getNumOfHashingThreads() {
                return Runtime.getRuntime().availableProcessors() * 2;
            }

            @Override
            public int getAcceptorPort() {
                return clientPort;
            }
        };

        // enable bootstrapping from public routers
        Module dhtModule = new DHTModule(new DHTConfig() {
            @Override
            public Collection<InetPeerAddress> getBootstrapNodes() {
                return Collections.singleton(new InetPeerAddress(config.getAcceptorAddress().getHostAddress(), serverPort));
            }
        });
        Path targetDirectory = Paths.get(fileDirPath);
        Storage storage = new FileSystemStorage(targetDirectory);
        StandaloneClientBuilder scb = Bt.client().config(config).storage(storage);

        if(torrentFilePath.indexOf("magnet:?xt=urn:btih:")>-1){
            //磁力
            scb= scb.magnet(torrentFilePath);
        }else{
            URL url = null;
            try {
                url=new File(torrentFilePath).toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            scb= scb.torrent(url);
        }
        if(isStopWhenDownloaded){
            scb= scb.stopWhenDownloaded();
        }
        bt.runtime.BtClient client = scb
                .autoLoadModules()
                .module(dhtModule)
                .afterTorrentFetched(torrent ->{
                    callback.call(torrent);
                })
                .randomizedRarestSelector()
                .build();

        client.startAsync(state -> {
            System.err.println("Peers: " + state.getConnectedPeers().size() + "; Downloaded: " + (((double)state.getPiecesComplete()) / state.getPiecesTotal()) * 100 + "%");
        }, 1000).join();
    }
    //默认停止
    public static void startNoStopWhenDownloaded(int clientPort,int serverPort,String torrentFilePath, String fileDirPath, Callback callback){
            start(clientPort,serverPort,false,torrentFilePath,fileDirPath,callback);
    }
    public static void main(String[] args) {

        String baseUrl = "src/main/resources";
        String targetDirPath=baseUrl + "/video/"+ Math.random()*10;
        //String targetDirPath=baseUrl + "/video";
        startNoStopWhenDownloaded(6991,6891, "magnet:?xt=urn:btih:de1a5c49d191cb15b3ee914ca71cf7f43d0ff407", targetDirPath, new Callback() {
            @Override
            public Object call(Object param) {
                Torrent torrent = (Torrent) param;
                System.err.println("magnet:?xt=urn:btih:" + torrent.getTorrentId());
                return null;
            }

        });
    }
}
