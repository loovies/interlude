package com.interlude.utils;

import com.interlude.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

// 用于处理与进程相关的操作，如执行系统命令
public class ProcessUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProcessUtils.class);

    public static String executeCommand(String cmd, Boolean outprintLog) {
        if (StringTools.isEmpty(cmd)) {
            logger.error("--- 指令执行失败, 因为要执行的FFMpeg指令为空 ! ---");
            return null;
        }

        // 使用Java的 Runtime 类的静态方法 exec() 执行给定的系统命令 cmd，返回一个 Process 对象。这是执行外部命令的标准方式。
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            // 执行ffmpeg指令
            // 取出输出流和错误流的信息
            // 注意, 必须要取出ffmpeg在执行命令过程中产生的输出信息,如果不取的话当输出流信息填满jvm储存输出流信息的缓冲区时,线程就会阻塞住
            // 分别处理进程的错误流和输出流。这两个对象继承自 Thread，异步地读取流数据，避免阻塞主线程。
            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputStream.start();

            // 等待ffmpeg 命令执行完 用 process.waitFor() 方法等待系统命令执行完成
            process.waitFor();
            // 获取执行结果字符串
            // 从 errorStream 和 inputStream 中获取数据，并将其拼接成一个完整的结果字符串。
            String result = errorStream.stringBuffer.append(inputStream.stringBuffer+"\n").toString();
            // 输出执行命令信息 根据 outprintLog 参数决定是否输出执行日志，以便追踪命令执行的情况。
            if(outprintLog){
                logger.info("执行命令:{}, 已执行完毕,执行结果:{}",cmd,result);
            }else{
                logger.info("执行命令:{}. 已执行完毕",cmd);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("视频转换失败");
        } finally {
            /**
             * 在 ProcessUtils 类的finally块中，将 ProcessKiller 对象添加为Shutdown Hook。
             * Shutdown Hook是一个特殊的线程，它在Java虚拟机即将关闭时执行。
             * 这样做的好处是，无论程序是正常退出还是异常退出，都能保证在退出前销毁正在执行的进程，从而避免资源泄露和未完成的进程。
             */
            if (null != process) {
                ProcessKiller ffmpegKiller = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }

    /**
     * 在程序退出前结束已有的FFmpeg进程
     * 实现多线程通常需要重写Thread类的run()方法，因为这是定义线程执行内容的地方
     * 在Java中，Thread类的run方法定义了线程的主体逻辑，当线程被启动（通过调用start方法）时，就会执行run方法中的代码。
     * ProcessKiller 类的主要作用是销毁给定的进程（Process对象）。在Java中，销毁一个进程意味着终止其运行，并释放其占用的系统资源，如内存和文件描述符等。
     * 在 ProcessKiller 类中，通过重写Thread类的run方法，可以将进程销毁的逻辑写在run方法中。
     * 这样，在程序退出时，通过调用 process.destroy() 方法，就可以确保进程被正确地销毁。
     */
    private static class ProcessKiller extends Thread {
        private Process process;

        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
        }
    }

    /**
     * 用于取出ffmpeg线程执行过程中产生的各种输出和错误流的信息
     */
    static class PrintStream extends Thread {
        InputStream inputStream = null;     // 用于接收输入流，即命令执行过程中产生的输出流或错误流
        BufferedReader bufferedReader = null;   // 用于从输入流中读取数据的缓冲读取器
        StringBuffer stringBuffer = new StringBuffer();     // 用于存储从输入流中读取的数据，采用线程安全的 StringBuffer 类

        public PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        // run() 方法是 Thread 类的抽象方法，PrintStream 类继承自 Thread，因此需要重写 run() 方法以定义线程的主要逻辑。
        @Override
        public void run() {
            try {
                if (null == inputStream) {
                    return;
                }
                // 如果 inputStream 不为null，则创建一个 BufferedReader 对象，用于逐行读取 inputStream 中的数据
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;     // 使用 readLine() 方法从流中读取每一行数据，并将其追加到 stringBuffer 中。
                while((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line);
                }
            }catch (Exception e){
                logger.error("读取输入流出错了! 错误信息:"+ e.getMessage());
            }finally {
                try {
                    if(null != bufferedReader){
                        bufferedReader.close();
                    }
                    if(null != inputStream){
                        inputStream.close();
                    }
                }catch (IOException e){
                    logger.error("调用PrintStream读取输出流后, 关闭流时出错 !");
                }
            }
        }
    }
}
