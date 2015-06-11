package com.harmazing.intelligentpow.db;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/15.
 */
public interface DBManager {


    public String findByHead(String head);

    public void addItem(String head, String headId);

//    public void deleteItem(String id);

//    public Boolean findCityById(String id);

    public void updateItem(String id);


}

