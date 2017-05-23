//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.jdbc;

import java.io.Serializable;
import java.sql.Connection;

public interface ConnectionProvider extends Serializable {
    void prepare();

    Connection getConnection();
}
