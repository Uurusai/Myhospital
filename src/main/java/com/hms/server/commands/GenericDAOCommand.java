package com.hms.server.commands;

import com.hms.dao.DAOCommand;
import com.hms.dao.DAOProvider;

import java.lang.reflect.Method;

// In com.hms.server.commands package
public class GenericDAOCommand<T> implements DAOCommand {
    private final String daoType;
    private final String methodName;
    private final Object[] args;
    private final Class<T> returnType;

    public GenericDAOCommand(String daoType, String methodName, Object[] args, Class<T> returnType) {
        this.daoType = daoType;
        this.methodName = methodName;
        this.args = args;
        this.returnType = returnType;
    }

    @Override
    public Object execute(DAOProvider provider) {
        try {
            Object dao = switch (daoType.toLowerCase()) {
                case "doctor" -> provider.getDoctorDAO();
                case "patient" -> provider.getPatientDAO();
                case "schedule" -> provider.getDoctorScheduleDAO();
                default -> throw new IllegalArgumentException("Unknown DAO type: " + daoType);
            };

            Method method = findMethod(dao.getClass(), methodName, args);
            return method.invoke(dao, args);
        } catch (Exception e) {
            throw new RuntimeException("Command execution failed", e);
        }
    }

    private Method findMethod(Class<?> clazz, String methodName, Object[] args) throws NoSuchMethodException {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                if (method.getParameterCount() == args.length) {
                    return method;
                }
            }
        }
        throw new NoSuchMethodException(methodName);
    }
}