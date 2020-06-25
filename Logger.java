import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
    private static Logger logger = null;
    private static FileWriter writer;

    private Logger() {}

    public static Logger getInstance() {
        String fileName = "/home/victor/Data/Java/log/FirstComers.log";
        if (logger == null) {
            logger = new Logger();
            try {
                File file = new File(fileName);
                if ( ! file.exists()) {
                    file.createNewFile();
                    writer = new FileWriter(fileName);
                } else {
                    writer = new FileWriter(fileName, true);
                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        return logger;
    }

    public synchronized void write(String message) {
        if (writer == null) {
            return;
        };
        try {
            writer.write(message);
            this.write();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public synchronized void write() {
        if (writer == null) {
            return;
        };
        try {
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public synchronized void writeTime(Date date) {
        this.writeTime(date.getTime());
    }

    public synchronized void writeTime(long l) {
        if (writer == null) {
            return;
        };
        long s = l / 1000;
        long m = s / 60; s = s % 60;
        long h = m / 60; m = m % 60; h = h % 24;
        String str = String.format("%d:%d:%d", h, m, s);
        try {
            writer.write(str);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public synchronized void write(String message, boolean flagTime) {
        if (flagTime) {
            writeTime(new Date().getTime());
            try {
                writer.write(". ");
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        write(message);
    }

    public synchronized void start() {
        String fileName = "/home/victor/Data/Java/log/FirstComers.log";
        if (logger == null) {
            logger = new Logger();
        } else {
            close();
        }
        try {
            File file = new File(fileName);
            if ( ! file.exists()) {
                file.createNewFile();
                writer = new FileWriter(fileName);
            } else {
                writer = new FileWriter(fileName, true);
            }
        } catch (IOException e) {
//                e.printStackTrace();
        }
    }
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
