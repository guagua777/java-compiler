package com.test;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CompileStringClass {

    public static Map<String, MemoryJavaFileObject>  compileResult = new HashMap<>();

    public void compile(String className, String sourceCode, String classPath) throws IOException {
        //File[] files1 = ... ; // input for first compilation task
        //File[] files2 = ... ; // input for second compilation task

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        //存储在项目根路径
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        //自定义存储，存储到memory中
        MemoryClassFileManager memoryClassFileManager = new MemoryClassFileManager(fileManager);


        SimpleJavaFileObject javaFileObject = new JavaSourceFromString(className, sourceCode);
        //Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(sourceCode));

        List<String> optionList = new ArrayList<String>();
        System.out.println("classPath is " + classPath);
        optionList = Arrays.asList("-encoding", "UTF-8", "-cp", classPath);
//        optionList.add("-Xlint");
        //optionList.add("unchecked");

        // 默认保存在项目根路径下
        // 需要将其保存到内存中
        Boolean r = compiler.getTask(null, /*fileManager*/memoryClassFileManager, null, optionList, null, Arrays.asList(javaFileObject))
                .call();

//        Iterable<? extends JavaFileObject> compilationUnits2 = fileManager.getJavaFileObjects(files2); // use alternative method
//        // reuse the same file manager to allow caching of jar files
//        compiler.getTask(null, fileManager, null, null, null, compilationUnits2).call();
        System.out.println("r ======================== is " + r);
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
        }
//        MemoryJavaFileObject memoryJavaFileObject = memoryClassFileManager.getMemoryJavaFileObject();
//        System.out.println(new String(memoryJavaFileObject.getBytes()));
        fileManager.close();
    }


    public /*static void main(String[] args)*/  void test(String classPath) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String sourceCode =
                "package com.test;\n" +
                        "public class Test {\n" +
                        "    public void test(){\n" +
                        "        System.out.println(\"test\");\n" +
                        "    }\n" +
                        "}";

//        sourceCode = "package com.test;\n" +
//                "\n" +
//                "public class DependModel2 {\n" +
//                "}";
//
//        String className = "com.test.DependModel2";
//        new CompileStringClass().compile(className, sourceCode);
//
        //MemoryClassLoader scl = new MemoryClassLoader();
        MemoryClassLoader scl = new MemoryClassLoader(getClass().getClassLoader());
//        Class<?> modelClass = scl.findClass(className);
//        Object modelObj = modelClass.newInstance();

        String sourceCodeTest = "package com.test;\n" +
                "\n" +
                "public class Test {\n" +
                "\n" +
                "    public void test(DependModel model) {\n" +
                "        System.out.println(model);\n" +
                "    }\n" +
                "}";

        String classNameTest = "com.test.Test";
        new CompileStringClass().compile(classNameTest, sourceCodeTest, classPath);

        System.out.println("编译完成：" + compileResult);

        System.out.println("system class path is " + System.getProperty("java.class.path") + "  ======== end ");

        // 执行
        //MemoryClassLoader scl = new MemoryClassLoader();
        Class<?> aClassTest = scl.findClass(classNameTest);
        Object obj = aClassTest.newInstance();
        DependModel dm = new DependModel();
//        Method method = aClass.getMethod("test", null);
//        Object result = method.invoke(obj, null);

        Method method = aClassTest.getMethod("test", DependModel.class);
        Object result = method.invoke(obj, dm);
        System.out.println("result is " + result);


    }

}
