package com.app.rzm.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.app.rzm.R;
import com.rzm.commonlibrary.general.db.DaoSupportFactory;
import com.rzm.commonlibrary.general.db.IDaoSupport;
import com.rzm.commonlibrary.general.db.upgrade.UpdateManager;

public class TestDbUpgradeActivity extends AppCompatActivity {
    TextView textView;
    UpdateManager updateManager;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db_upgrade);
        textView = (TextView) findViewById(R.id.content);
        updateManager = new UpdateManager();
    }

    public void createV001(View view) {
        IDaoSupport<User> dao = DaoSupportFactory.getFactory(this).getDao(User.class);

        for (int i1 = 0; i1 < 10; i1++) {
            User user = new User();
            user.setName("张三-----V001" + (i++));
            user.setPassword("123456");
            dao.insert(user);
        }


        IDaoSupport<Photo> photoIDaoSupport = DaoSupportFactory.getFactory(this).getDao(Photo.class);

        for (int i1 = 0; i1 < 10; i1++) {
            Photo photo = new Photo();
            photo.setPath("data/data/my.jpg-----V001");
            photoIDaoSupport.insert(photo);
        }
    }

    public void update1to2(View view) {
        /**
         * 这个方法是模拟一个情景，当前有一个新版本发布，此时运行于市场上的app通过版本更新
         * 接口请求到了新版本信息，新版本版本号为V003,当前版本版本号为V002,通过这个方法将新旧版本
         * 号写入到一个文件中做记录，这个信息可以传递出此次升级是从哪个版本升级到哪个版本
         * 保存成功返回true，否则返回false
         */
        updateManager.saveVersionInfo("V001");
        updateManager.startUpdateDb(this);
    }

    public void update1to3(View view) {
        updateManager.saveVersionInfo("V001");
        updateManager.startUpdateDb(this);
    }

    public class User {

        public String name;

        public String password;

        public String user_id;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public class Photo {
        public String time;

        public String path;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

}
