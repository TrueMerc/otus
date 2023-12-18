package ru.ryabtsev.starship.context;

public class ApplicationContextHolder {

    ApplicationContext rootContext;

    ThreadLocal<ApplicationContext> currentContext;


}
