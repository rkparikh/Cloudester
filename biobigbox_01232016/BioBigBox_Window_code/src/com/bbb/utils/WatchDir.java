/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bbb.utils;

/**
 *
 * @author Totaram
 */
import com.bbb.init.Initialize;
import com.bbb.ui.MainAppFrame;
import com.sun.nio.file.ExtendedWatchEventModifier;
import java.io.File;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * WatchDir CLASS USE FOR a directory (or tree) for changes to files.
 */
public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd_HH.mm.ss");
    final static Logger logger = Logger.getLogger(WatchDir.class);
    public static String sourceDirectory = "";

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private void register(Path dir) throws IOException {
        WatchEvent.Kind kind[] = {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};
        WatchKey key = dir.register(watcher, kind, ExtendedWatchEventModifier.FILE_TREE);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                logger.error(String.format("register: %s\n", dir));
                //  System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    logger.info(String.format("update: %s -> %s\n", prev, dir));
                    // System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    WatchDir(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;
        if (recursive) {
            logger.error(String.format("Scanning %s ...\n", dir));
            registerAll(dir);
            logger.error("Done.");
        } else {
            register(dir);
        }

        this.trace = true;
    }

    @SuppressWarnings("unchecked")
    void processEvents() {
        for (;;) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            Path dir = keys.get(key);
            if (dir == null) {
                logger.error("WatchKey not recognized!!");
                continue;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                File file = new File(child.toString());
                String extension = "";

                int i = child.toString().lastIndexOf('.');
                if (i > 0) {
                    extension = child.toString().substring(i + 1);
                }
                System.out.println("Folder Watcher First " + child.toString() + " Event" + event.kind().name() + " extension :" + extension);

                if (event.kind().name().equals("ENTRY_CREATE") && file.isDirectory()) {
                    FileHandlerRead.printFiles(file);
                } else if (Initialize.firstTimeDownloadList.contains(child.toString())) {
                    Initialize.firstTimeDownloadList.remove(child.toString());
                } else if (!Initialize.uploadingfile.equals(child.toString()) && !"ENTRY_DELETE".equals(event.kind().name())) {
                    Initialize.tempMap.put(child.toString(), "ENTRY_MODIFY");
                } else if ("ENTRY_DELETE".equals(event.kind().name()) && !extension.trim().equals("")) {
                    Initialize.tempMap.put(child.toString(), "ENTRY_DELETE");
                } else if ("ENTRY_DELETE".equals(event.kind().name())) {
                    Initialize.tempMap.put(child.toString(), "ENTRY_DELETE_FOLDER");
                }
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (file.length() == 0 || file.getUsableSpace() == 0) {
                            //System.out.println("file name with o size:"+file+" and size is:"+file.length());
                            Initialize.tempMap.put(child.toString(), "ENTRY_DELETE");
                        }
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {

                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public static void watchSource(String directory) throws IOException {
        boolean recursive = true;
        Path dir = Paths.get(directory);
        sourceDirectory = directory;
        MainAppFrame.progressDialog.setVisible(false);
        logger.error("Watch Directory Call..............................................................");
        WatchDir watch = new WatchDir(dir, recursive);
        watch.processEvents();
//        for(;;){
//             BBBUtils.createDirectory(directory);
//            
//            
//        }

    }

}
