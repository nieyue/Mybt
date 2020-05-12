/*
 * Copyright (c) 2016—2018 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nieyue.datasharing;

import bt.Bt;
import bt.data.Storage;
import bt.data.StorageUnit;
import bt.data.file.FileSystemStorage;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentFile;
import bt.runtime.BtClient;
import bt.runtime.Config;
import bt.torrent.DefaultTorrentSessionState;

import java.nio.file.Paths;

public class Seeder {

    public static final int PORT = 6891;

    public static void main(String[] args) throws Exception {
        Config config = new Config() {
            @Override
            public int getNumOfHashingThreads() {
                return Runtime.getRuntime().availableProcessors() * 2;
            }

            @Override
            public int getAcceptorPort() {
                return PORT;
            }
        };
        //"src/main/resources/magnet/1.txt"
        Storage storage = new FileSystemStorage(Paths.get("src/main/resources/source"));
        //Storage storage = new FileSystemStorage(Paths.get("src", "main", "resources", "source"));
        BtClient client = Bt.client()
                .config(config)
                .storage(storage)
               // .torrent(Paths.get("src", "main", "resources", "torrent","1.torrent").toFile().toURI().toURL())
                .torrent(Paths.get("src/main/resources/torrent/1.torrent").toFile().toURI().toURL())
                .autoLoadModules()
                .afterTorrentFetched(torrent -> System.err.println("magnet:?xt=urn:btih:" + torrent.getTorrentId()))
                .build();

        System.err.println("Starting seeder...");
        client.startAsync(state -> {
            System.err.println("Peers: " + state.getConnectedPeers().size() + "; Downloaded: " + (((double)state.getPiecesComplete()) / state.getPiecesTotal()) * 100 + "%");
        }, 1000).join();
    }
}
