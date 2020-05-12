package com.nieyue;

import bt.Bt;
import bt.StandaloneClientBuilder;
import bt.TorrentClientBuilder;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentFile;
import bt.metainfo.TorrentSource;
import bt.runtime.BtClient;
import bt.runtime.Config;
import com.google.inject.Module;
import javafx.util.Callback;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * bt服务
 */
public class BtServer {
    /**
     * 开启
     * @param port 端口
     * @param torrentFilePath 种子文件路径
     * @param fileDirPath 文件目录路径
     * @param callback 回调
     */
    public static void start(int port, String torrentFilePath, String fileDirPath, Callback callback){

        // enable multithreaded verification of torrent data
        Config config = new Config() {
            @Override
            public int getNumOfHashingThreads() {
                return Runtime.getRuntime().availableProcessors() * 2;
            }

            @Override
            public int getAcceptorPort() {
                return port;
            }

        };

        // enable bootstrapping from public routers
      /*  Module dhtModule = new DHTModule(new DHTConfig() {
            @Override
            public boolean shouldUseRouterBootstrap() {
                return true;
            }
        });*/

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
        BtClient client = scb
                .autoLoadModules()
                //.module(dhtModule)
                .afterTorrentFetched(torrent ->{
                    callback.call(torrent);
                }).build();

        //client.startAsync().join();
        client.startAsync(state -> {
            System.err.println("Peers: " + state.getConnectedPeers().size() + "; Downloaded: " + (((double)state.getPiecesComplete()) / state.getPiecesTotal()) * 100 + "%");
        }, 1000).join();
    }

    public static void main(String[] args) {

        String baseUrl = "src/main/resources";
        start(6891, baseUrl + "/torrent/1.torrent", baseUrl + "/source", new Callback() {
            @Override
            public Object call(Object param) {
                Torrent torrent = (Torrent) param;
                System.err.println("magnet:?xt=urn:btih:" + torrent.getTorrentId());
                return null;
            }

        });
    }
}
