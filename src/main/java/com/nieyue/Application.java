package com.nieyue;

import bt.Bt;
import bt.TorrentClientBuilder;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.magnet.MagnetUri;
import bt.magnet.MagnetUriParser;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentSource;
import bt.peerexchange.PeerExchangeConfig;
import bt.peerexchange.PeerExchangeModule;
import bt.processor.magnet.MagnetContext;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;
import bt.runtime.Config;
import com.google.inject.Module;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class Application {
    static String baseUrl = "src/main/resources";
    //私有运行时的客户端
    public static void self(){

        // enable multithreaded verification of torrent data
        Config config = new Config() {
            @Override
            public int getNumOfHashingThreads() {
                return Runtime.getRuntime().availableProcessors() * 2;
            }
        };

        //config.setAcceptorPort(6899);
        // enable bootstrapping from public routers
        Module dhtModule = new DHTModule(new DHTConfig() {
            @Override
            public boolean shouldUseRouterBootstrap() {
                return true;
            }
        });

        //Path targetDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
        Path targetDirectory = Paths.get(baseUrl+"/video/1.mp4");
      //  Path targetDirectory = Paths.get(baseUrl+"/video");
        Storage storage = new FileSystemStorage(targetDirectory);
        URL url = null;
        try {
             //url=new URL("https://tbm-auth.alicdn.com/e99361edd833010b/IVDv9na5AeqibRSfuOP/gWmhp0e8gzFNFFM5q8J_251309133700_hd_hq.mp4?auth_key=1588749222-0-0-3d44ba0ff40b71d3f919f2c7e2efd3c0");
            url=new File(baseUrl+"/torrent/1.torrent").toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        BtClient client = Bt.client()
                .storage(storage)
               //.torrent(url)
               // .magnet("magnet:?xt=urn:btih:A7CDEDE5F3468A93BA39B06BA209CD997CDB29BF&dn=SQTE-021&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80&tr=udp%3A%2F%2Ftracker.ccc.de%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80%2Fannounce")
                .magnet("magnet:?xt=urn:btih:5dbde2ccce0bcdd9d9ca30b2db3c1bb51b9b7410")
                .autoLoadModules()
                .module(dhtModule)
                .stopWhenDownloaded()
                .build();

        client.startAsync().join();
    }

    public static void init(){
        PeerExchangeConfig config = new PeerExchangeConfig() {
            @Override
            public int getMinEventsPerMessage() {
                // don't send PEX message if there are less than 50 added/dropped peer events
                return 50;
            }
        };

        PeerExchangeModule customModule = new PeerExchangeModule(config);
        BtRuntime runtime = BtRuntime.builder().module(customModule).build();
        runtime.startup();
    }
    public static void main(String[] args) {
        init();

        self();
    }
}
