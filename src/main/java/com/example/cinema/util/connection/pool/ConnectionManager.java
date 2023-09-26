package com.example.cinema.util.connection.pool;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public final class ConnectionManager {
    private final String PASSWORD_KEY;
    private final String USERNAME_KEY;
    private final String URL_KEY;
    private final int POOL_SIZE_KEY;
    private BlockingQueue<Connection> pool;
    private List<Connection> sourceConnections;

    {
        loadDriver();
    }

    @Autowired
    public ConnectionManager(
            @Value("${db.password}") String PASSWORD_KEY,
            @Value("${db.username}") String USERNAME_KEY,
            @Value("${db.url}") String URL_KEY,
            @Value("${db.pool.size}") int POOL_SIZE_KEY) {


        this.PASSWORD_KEY = PASSWORD_KEY;
        this.USERNAME_KEY = USERNAME_KEY;
        this.URL_KEY = URL_KEY;
        this.POOL_SIZE_KEY = POOL_SIZE_KEY;
    }

    @PostConstruct
    public void initConnectionPool() {
        var poolSize = POOL_SIZE_KEY;
        pool = new ArrayBlockingQueue<>(poolSize);
        sourceConnections = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            var connection = open();
            var proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(), new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close")
                            ? pool.add((Connection) proxy)
                            : method.invoke(connection, args));
            pool.add(proxyConnection);
            sourceConnections.add(connection);
        }
    }

    public Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection open() {
        try {
            return DriverManager.getConnection(URL_KEY, USERNAME_KEY, PASSWORD_KEY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void closePool() {
        for (Connection sourceConnection : sourceConnections) {
            try {
                sourceConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}