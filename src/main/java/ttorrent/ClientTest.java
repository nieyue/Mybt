package ttorrent;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTest {
   static String baseUrl="E:\\nieyue\\IntelliJIDE\\work\\Mybt\\src\\main\\resources";
    //static String baseUrl = "E:\\nieyue\\ide\\bt\\bt-cli";
    public static void start(final Long id) throws IOException, NoSuchAlgorithmException {

// First, instantiate the Client object.
        Client client = new Client(
                // This is the interface the client will listen on (you might need something
                // else than localhost here).
                InetAddress.getLocalHost(),

                // Load the torrent from the torrent file and use the given
                // output directory. Partials downloads are automatically recovered.
                SharedTorrent.fromFile(
                        new File(baseUrl+"\\tester.rmvb.torrent"),
                        new File(baseUrl)));

// You can optionally set download/upload rate limits
// in kB/second. Setting a limit to 0.0 disables rate
// limits.
        client.setMaxDownloadRate(50.0);
        client.setMaxUploadRate(50.0);

// At this point, can you either call download() to download the torrent and
// stop immediately after...
        client.download();
// Or call client.share(...) with a seed time in seconds:
 //client.share(3600);
// Which would seed the torrent for an hour after the download is complete.
        client.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Client client = (Client) observable;
                float progress = client.getTorrent().getCompletion();
                // Do something with progress.
                System.out.println(id+"进度="+progress);
            }
        });
// Downloading and seeding is done in background threads.
// To wait for this process to finish, call:
        client.waitForCompletion();

// At any time you can call client.stop() to interrupt the download.

    }

    public static void t() throws IOException, NoSuchAlgorithmException {
        // First, instantiate a Tracker object with the port you want it to listen on.
// The default tracker port recommended by the BitTorrent protocol is 6969.
        Tracker tracker = new Tracker(new InetSocketAddress(6969));

// Then, for each torrent you wish to announce on this tracker, simply created
// a TrackedTorrent object and pass it to the tracker.announce() method:
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".torrent");
            }
        };

        for (File f : new File(baseUrl).listFiles(filter)) {
            tracker.announce(TrackedTorrent.load(f));
        }

// Once done, you just have to start the tracker's main operation loop:
        tracker.start();

// You can stop the tracker when you're done with:
       // tracker.stop();
    }
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        t();
        ExecutorService threads = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 1; i++) {
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        start(Thread.currentThread().getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
