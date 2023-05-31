package com.test;

public class MemoryClassLoader extends ClassLoader {

    //构造函数中没有指定父加载器
    //所以需要在findclass中单独定义

//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        MemoryJavaFileObject fileObject = CompileStringClass.compileResult.get(name);
//        System.out.println("name is   " + name + "   fileObject is ======== " + fileObject);
//        if (fileObject != null) {
//            byte[] bytes = fileObject.getBytes();
//            return defineClass(name, bytes, 0, bytes.length);
//        }
//
//        //return null;
//
//        // 其他类
//        try {
//            //return getClass().getClassLoader().loadClass(name);
//            return Thread.currentThread().getContextClassLoader().loadClass(name);
//        } catch (Exception e) {
//            return super.findClass(name);
//        }
//    }

    //或者采用下面这种方式，构造函数中添加父加载器

    private ClassLoader parent;


    public MemoryClassLoader(ClassLoader parent) {
        super(parent);
        this.parent = parent;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        MemoryJavaFileObject fileObject = CompileStringClass.compileResult.get(name);
        System.out.println("name is   " + name + "   fileObject is ======== " + fileObject);
        if (fileObject != null) {
            byte[] bytes = fileObject.getBytes();
            return defineClass(name, bytes, 0, bytes.length);
        } else {
            return parent.loadClass(name);
        }
    }



}
