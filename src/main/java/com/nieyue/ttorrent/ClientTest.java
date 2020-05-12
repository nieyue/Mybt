package com.nieyue.ttorrent;

import com.nieyue.util.FileUtil;
import com.turn.ttorrent.client.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * torrent客户
 */
public class ClientTest {


    /**
     * 客户端启动
     * @param torrentFilePath torrent文件路径
     * @param targetDirPath  目标文件目录
     * @throws IOException
     */
    public static void start(String torrentFilePath,String targetDirPath) throws IOException {
        FileUtil.judeDirExists(new File(targetDirPath));
        SimpleClient client = new SimpleClient();
        InetAddress address = InetAddress.getLocalHost();
        //InetAddress address =InetAddress.getByName("127.0.0.1");
        try {
            TorrentManager result = client.downloadTorrentAsync(
                    torrentFilePath,
                    targetDirPath
                    , address
            );

            result.addListener(new TorrentListener() {
                @Override
                public void peerConnected(PeerInformation peerInformation) {
                    System.out.println("peerConnected start!");
                    System.out.println("peerInformation getAddress="+peerInformation.getAddress());
                   // System.out.println("peerInformation getClientVersion="+peerInformation.getClientVersion());
                    //System.out.println("peerInformation getClientIdentifier="+peerInformation.getClientIdentifier());
                    System.out.println("peerConnected end!");
                }

                @Override
                public void peerDisconnected(PeerInformation peerInformation) {
                    System.out.println("peerDisconnected start!");
                    System.out.println("peerInformation getAddress="+peerInformation.getAddress());
                   // System.out.println("peerInformation getClientVersion="+peerInformation.getClientVersion());
                 //   System.out.println("peerInformation getClientIdentifier="+peerInformation.getClientIdentifier());
                    System.out.println("peerDisconnected end!");
                }

                @Override
                public void pieceDownloaded(PieceInformation pieceInformation, PeerInformation peerInformation) {
                    System.out.println("pieceDownloaded start!");
                    System.out.println("pieceInformation getSize="+pieceInformation.getSize());
                    System.out.println("pieceInformation getIndex="+pieceInformation.getIndex());
                    System.out.println("peerInformation getId="+new String(peerInformation.getId()));
                    System.out.println("peerInformation getAddress="+peerInformation.getAddress());
                   // System.out.println("peerInformation getClientVersion="+peerInformation.getClientVersion());
                    //System.out.println("peerInformation getClientIdentifier="+peerInformation.getClientIdentifier());
                    System.out.println("pieceDownloaded end!");
                }

                @Override
                public void downloadComplete() {
                    System.out.println("downloadComplete");
                   // client.stop();
                    System.out.println("stop success");
                }

                @Override
                public void pieceReceived(PieceInformation pieceInformation, PeerInformation peerInformation) {
                    System.out.println("pieceReceived start!");
                    System.out.println("pieceInformation getSize="+pieceInformation.getSize());
                    System.out.println("pieceInformation getIndex="+pieceInformation.getIndex());
                    System.out.println("peerInformation getId="+new String(peerInformation.getId()));
                    System.out.println("peerInformation getAddress="+peerInformation.getAddress());
                   // System.out.println("peerInformation getClientVersion="+peerInformation.getClientVersion());
                   // System.out.println("peerInformation getClientIdentifier="+peerInformation.getClientIdentifier());
                    System.out.println("pieceReceived end!");
                }

                @Override
                public void downloadFailed(Throwable throwable) {
                    System.out.println("downloadFailed"+throwable.getMessage());
                }

                @Override
                public void validationComplete(int i, int i1) {
                    System.out.println("validationComplete"+i+"-----"+i1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //client.stop();
    }


    public static void main(String[] args) throws IOException {
         String baseUrl = "src/main/resources";
        //start(baseUrl + "/torrent/1.torrent",baseUrl + "/video");
        ExecutorService threads = Executors.newFixedThreadPool(10);
        for (int i = 0; i <2; i++) {
            int finalI = i;
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //start(baseUrl + "/torrent/1.torrent",baseUrl + "/video/"+String.valueOf(finalI));
                        start(baseUrl + "/torrent/old/EP60.torrent",baseUrl + "/video/"+String.valueOf(finalI));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
