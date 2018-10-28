package com.development.borissu.demoapp.activities.database;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;
import com.development.borissu.demoapp.adapter.DbResultAdapter;
import com.development.borissu.demoapp.database.MyDataBase;
import com.development.borissu.demoapp.database.dao.UserDao;
import com.development.borissu.demoapp.database.entity.User;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class DatabaseActivity extends BaseActivity {

    final String INSERT_RESULT_STR = "共有%d筆資料被新增";
    final String QUERY_RESULT_STR = "共%d筆資料";
    final String UPDATE_RESULT_STR = "共有%d筆資料被更新";
    final String DELETE_RESULT_STR = "共有%d筆資料被刪除";

    MyDataBase db;
    UserDao userDao;

    @BindView(R.id.et_id)
    EditText idInput;
    @BindView(R.id.et_first_name)
    EditText firstNameInput;
    @BindView(R.id.et_last_name)
    EditText lastNameInput;
    @BindView(R.id.tv_query_result)
    TextView queryResult;
    @BindView(R.id.rv_result_list)
    RecyclerView resultList;

    DbResultAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        db = MyDataBase.getINSTANCE(this);
        userDao = db.getUserDao();

        adapter = new DbResultAdapter(this);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        resultList.setAdapter(adapter);

    }

    @OnClick(R.id.btn_insert)
    public void onClickInsert() {
        String id = idInput.getText().toString();
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        User user = new User(id, firstName, lastName);
        user.setCreateDate(new Date());
        userDao.inserUsers(user);
    }


    @OnClick(R.id.btn_query)
    public void onClickQuery() {

        List<User> queryResultList = userDao.getAllUsers();

        if (queryResultList != null) {
            queryResult.setText(String.format(QUERY_RESULT_STR, queryResultList.size()));

            adapter.setDataList(queryResultList);

        }
    }

    @OnClick(R.id.btn_update)
    public void onClickUpdate() {
        String id = idInput.getText().toString();
        User u = userDao.getUser(id);
        if (u == null) {
            u = new User();
            u.setCreateDate(new Date());
        }
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        userDao.updateUsers(u);
    }

    @OnClick(R.id.btn_delete)
    public void onClickDelete() {
        userDao.deleteUsers(new User(idInput.getText().toString()));

    }
}
