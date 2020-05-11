package com.nieyue.ttorrent;

import com.nieyue.util.CommunicationManagerFactory;
import com.turn.ttorrent.client.CommunicationManager;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * torrent服务
 */
public class TorrentServer {
    private static int defaultPort=6069;
    static List<CommunicationManager> communicationManagerList= new ArrayList<>();
    static  CommunicationManagerFactory communicationManagerFactory=new CommunicationManagerFactory();

    /**
     * 启动
     * @param announceUrl 发布url
     * @param port 端口
     * @param torrentDirPath 种子目录路径
     * @param fileDirPath 文件目录路径
     * @throws IOException
     */
    public static void start(String announceUrl, int port,String torrentDirPath,String fileDirPath) throws IOException {
        Tracker tracker = new Tracker(port==0?defaultPort:port);
        if(announceUrl!=null&&"".equals(announceUrl)){
            //"http://localhost:6969/announce"
            tracker = new Tracker(port,announceUrl);
        }
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                //System.out.println(dir.toURI().getPath());
                return name.endsWith(".torrent");
            }
        };
        CommunicationManager seederCommunicationManager = communicationManagerFactory.getClient("");
        for (File f : new File(torrentDirPath).listFiles(filter)) {
            System.out.println(f.toURI().getPath());
            tracker.announce(TrackedTorrent.load(f));
            //分享

            communicationManagerList.add(seederCommunicationManager);
            seederCommunicationManager.addTorrent(f.toURI().getPath(),fileDirPath);

        }
        //发布文件开始
        seederCommunicationManager.start(InetAddress.getLocalHost());
        System.out.println("getAnnounceUrl="+tracker.getAnnounceUrl());
        //Also you can enable accepting foreign torrents.
        //if tracker accepts request for unknown torrent it starts tracking the torrent automatically
        tracker.setAcceptForeignTorrents(true);

        // Once done, you just have to start the tracker's main operation loop:
        //发布种子开始
        tracker.start(true);

        // You can stop the tracker when you're done with:
        // tracker.stop();
    }
    public static void start(int port,String torrentDirPath,String fileDirPath) throws IOException {
        start(null,port,torrentDirPath,fileDirPath);
    }
    public static void main(String[] args) throws IOException {
         String baseUrl = "src/main/resources";
            start(6969,baseUrl+"/torrent",baseUrl+"/source");
    }

}
