package com.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        final URL resource = Thread.currentThread().getContextClassLoader().getResource("pathflag.txt");
        System.out.println("resource path is " + resource.getPath());
        System.out.println(System.getProperty("java.io.tmpdir"));
        System.out.println(System.getProperty("java.class.path"));
        System.out.println(System.getProperty("java.library.path"));

        String rootPath = "";//resource.getPath().substring(5, resource.getPath().indexOf("!/BOOT-INF/"));
        rootPath = "/Users/projects/java-compiler/target/java-compiler-1.0-SNAPSHOT.jar";
        System.out.println("rootPath  is " + rootPath);





        File tempFolder = new File(System.getProperty("java.io.tmpdir"));
        File folder = new File(tempFolder, "test-loader-" + UUID.randomUUID());
//		File folder = new File(tempFolder, "test-loader-tmp");
        System.out.println("【rootJarPath】:{}" + rootPath);
        System.out.println("【tempFolder】:{}" + tempFolder.getPath());
        System.out.println("【folder】:{}" + folder.getPath());
        System.out.println("【folder.exists】:{}" + folder.exists());

        if (!folder.mkdirs() /*|| !activationsFolder.mkdirs()*/) {
            throw new RuntimeException("can not mkdir temp dir");
        }
        try {
            unzipJar(folder.getAbsolutePath(), rootPath);
        } catch (IOException e) {
            System.out.println("cannot unzipJar,jarFilePath:" + rootPath);
            throw new RuntimeException("cannot unzipJar file");
        }
        File folder_lib = new File(folder.getAbsolutePath() + "/BOOT-INF/lib");
        File[] fs = folder_lib.listFiles();
        StringBuffer sb = new StringBuffer();
        for (File f : fs) {
            sb.append(f.getAbsolutePath()).append(":");
        }
        String cpStr = sb + folder.getAbsolutePath() + "/BOOT-INF/classes";
        System.out.println("........**.........");
        System.out.println("【build得到的ClassPath】:{}" + cpStr.replaceFirst("\\:$", ""));
        System.out.println("...................");
        //System.out.println(cpStr);



        CompileStringClass compileStringClass = new CompileStringClass();
        //compileStringClass.classPath = cpStr;
        try {
            compileStringClass.test(cpStr);
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * SpringBoot程序启动后,返回值是ConfigurableApplicationContext,它也是一个Spring容器
         * 它其实相当于原来Spring容器中启动容器ClasspathXmLApplicationContext
         */
        //获取SpringBoot容器
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);

    }


    public static void unzipJar(String destinationDir, String jarPath) throws IOException {
        File file = new File(jarPath);
        JarFile jar = new JarFile(file);
        for (Enumeration enums = jar.entries(); enums.hasMoreElements(); ) {
            JarEntry entry = (JarEntry) enums.nextElement();
            String fileName = destinationDir + File.separator + entry.getName();
            File f = new File(fileName);
            if (fileName.endsWith("/")) {
                f.mkdirs();
            }
        }
        //now create all files
        for (Enumeration enums = jar.entries(); enums.hasMoreElements(); ) {
            JarEntry entry = (JarEntry) enums.nextElement();
            String fileName = destinationDir + File.separator + entry.getName();
            File f = new File(fileName);
            if (!fileName.endsWith("/")) {
                unpack(jar, entry, f);
            }
        }
    }

    // 解压 jar 包;
    private static void unpack(JarFile jarFile, JarEntry entry, File file) throws IOException {
        try (InputStream inputStream = jarFile.getInputStream(entry)) {
            try (OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        }
    }


}
