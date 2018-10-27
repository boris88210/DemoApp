package com.development.borissu.demoapp.activities.database;

import android.os.Bundle;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;
import com.development.borissu.demoapp.database.MyDataBase;
import com.development.borissu.demoapp.database.dao.UserDao;
import com.development.borissu.demoapp.database.entity.User;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class DatabaseActivity extends BaseActivity {

    MyDataBase db;
    UserDao userDao;

    @BindView(R.id.tv_query_result)
    TextView queryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
    }


    @OnClick(R.id.btn_query)
    public void onClickBtn() {
        db = MyDataBase.getINSTANCE(this);
        User user1 = new User("1", "Doris", "Su");
        userDao = db.getUserDao();
        userDao.inserUsers(user1);

        String firstName = userDao.getUser("1").getFirstName();
        queryResult.setText(firstName);


        User user2 = userDao.getUser("2");
        boolean result = user2 == null;

        User user3 = new User("3", "BOB", "123");
        userDao.inserUsers(user3);
        List<User> userList = userDao.getAllUsers();

        User user4 = new User("3", "COC", "321");
        userDao.deleteUsers(user4);
        userList = userDao.getAllUsers();

    }
}
