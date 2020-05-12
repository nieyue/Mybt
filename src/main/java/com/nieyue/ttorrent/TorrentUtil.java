package com.nieyue.ttorrent;

import com.turn.ttorrent.common.TorrentCreator;
import com.turn.ttorrent.common.TorrentMetadata;
import com.turn.ttorrent.common.TorrentSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 */
public class TorrentUtil {
    /**
     *  生成torrent文件
     * @param announceList tracker 地址
     * @param inputfilepath 输入文件地址
     * @param torrentFilePath 种子文件地址
     * @param createdBy
     */
    public static void createTorrentFile(
            List<URI> announceList,
            String inputfilepath,
            String torrentFilePath,
            String createdBy
    ){
        try {
            TorrentMetadata torrent;
            if(announceList.size()<=0){
                throw new RuntimeException("缺少tracker链接");
            }else if(announceList.size()==1){
                torrent = TorrentCreator.create(new File(inputfilepath), announceList.get(0), createdBy);
            }else{
                List<List<URI>> list=new ArrayList<>();
                list.add(announceList);
                torrent = TorrentCreator.create(new File(inputfilepath),list,createdBy);
            }
            File torrentFile = new File(torrentFilePath);
            FileOutputStream fos = new FileOutputStream(torrentFile);
            fos.write(new TorrentSerializer().serialize(torrent));
            fos.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<URI> announceList=new ArrayList<>();
        URI uri=URI.create("http://localhost:6969");
        announceList.add(uri);
        String baseUrl = "src/main/resources";
        String inputfilepath=baseUrl+"/source/1.mp4";
        String torrentFilePath=baseUrl+"/torrent/1.torrent";
        String createdBy="nieyue";
        createTorrentFile(announceList,inputfilepath,torrentFilePath,createdBy);
    }
}
