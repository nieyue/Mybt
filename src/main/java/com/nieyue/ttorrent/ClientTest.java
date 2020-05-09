package com.nieyue.ttorrent;

import com.turn.ttorrent.client.SimpleClient;
import com.turn.ttorrent.common.TorrentCreator;
import com.turn.ttorrent.common.TorrentMetadata;
import com.turn.ttorrent.common.TorrentSerializer;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ClientTest {
   //static String baseUrl="E:\\nieyue\\IntelliJIDE\\work\\Mybt\\src\\main\\resources";
    static String baseUrl = "E:\\nieyue\\ide\\Mybt\\src\\main\\resources";
    public static void start(final Long id) throws IOException {

        SimpleClient client = new SimpleClient();
       // InetAddress address = InetAddress.getLocalHost();
        InetAddress address =InetAddress.getByName("127.0.0.1");
        try {
            client.downloadTorrent(baseUrl+"\\torrent\\1.torrent",
                           // baseUrl+"\\torrent\\EP60.torrent",
                                baseUrl+"\\source"+(id==0?"\\"+id:"")
                                ,address
                    );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.stop();

    }

    public static void t() throws IOException, NoSuchAlgorithmException {
        Tracker tracker = new Tracker(6969);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".torrent");
            }
        };

        for (File f : new File(baseUrl+"\\torrent\\").listFiles(filter)) {
            System.out.println(f.toURI().getPath());
            tracker.announce(TrackedTorrent.load(f));

        }
        System.out.println("getAnnounceUrl="+tracker.getAnnounceUrl());
        //Also you can enable accepting foreign torrents.
        //if tracker accepts request for unknown torrent it starts tracking the torrent automatically
        tracker.setAcceptForeignTorrents(true);

        // Once done, you just have to start the tracker's main operation loop:
        tracker.start(true);

        // You can stop the tracker when you're done with:
        tracker.stop();
    }

    public static void createTorrentFile(){
        List<URI> announceList=new ArrayList<>();
        URI uri = URI.create("http://localhost:6969/announce");
       // URI uri = URI.create("http://bt.3dmgame.com:2710/announce");
        //URI uri = URI.create("udp://tracker.publicbt.com:80/announce");
        URI uri2 = URI.create("udp://tracker.openbittorrent.com:80/announce");
        announceList.add(uri);
        announceList.add(uri2);
        String inputfilepath="E:\\nieyue\\ide\\Mybt\\src\\main\\resources\\source\\1.mp4";
        String outfilepath="E:\\nieyue\\ide\\Mybt\\src\\main\\resources\\torrent\\1.torrent";
        try {
            TorrentMetadata torrent = TorrentCreator.create(new File(inputfilepath), uri, "BitComet/1.28");
            File torrentFile = new File(outfilepath);
            FileOutputStream fos = new FileOutputStream(torrentFile);
            fos.write(new TorrentSerializer().serialize(torrent));
            fos.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        t();
        //createTorrentFile();
        start(0l);
        ExecutorService threads = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        start(Thread.currentThread().getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //Torrent to = Torrent.load(new File("E:\\nieyue\\ide\\Mybt\\src\\main\\resources\\torrent\\1.torrent"));
       // System.out.println(to.toString());
    }
}
