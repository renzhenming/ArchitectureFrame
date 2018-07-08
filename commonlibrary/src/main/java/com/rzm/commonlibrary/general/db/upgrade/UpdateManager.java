package com.rzm.commonlibrary.general.db.upgrade;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;


import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class UpdateManager {
    /**
     * 数据库存放的根目录 目前是内存卡下的一个目录ROOT_DB_DIR
     */
    private final String ROOT_DB_DIR = "update";
    /**
     * 数据库备份的目录ROOT_BACK_UP_DIR，存放于ROOT_DB_DIR中
     */
    private final String ROOT_BACK_UP_DIR = "backDb";
    /**
     * 版本更新的相关信息存放文件
     */
    private final String UPDATE_INFO = "update.txt";
    /**
     * 升级用的脚本文件
     */
    private final String UPDATE_SCRIPT_XML = "updateXml.xml";
    private File parentFile = new File(Environment.getExternalStorageDirectory(), ROOT_DB_DIR);

    private File bakFile = new File(parentFile, ROOT_BACK_UP_DIR);

    public UpdateManager() {
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!bakFile.exists()) {
            bakFile.mkdirs();
        }

    }

    public void checkThisVersionTable(Context context) {
        //解析mxl文件，将解析到的信息封装到UpdateDbXml对象中
        UpdateDbXml xml = readDbXml(context);
        //获取当前版本信息
        String thisVersion = this.getVersionName(context);
        //获取到和当前升级版本匹配的CreateVersion，xml中的每一个createVersion都对应这一个
        //版本的升级处理,这里第二个参数表示的是升级后的版本号，为什么是升级后的，因为执行到这一步
        //时，是已经下载了新版本apk，覆盖安装之后读取了安装后的apk的版本号，有了这个版本号我们
        //就知道相应的数据库应该升级到哪个版本，然后选取相应的CreateVersion对象进行操作
        CreateVersion thisCreateVersion = analyseCreateVersion(xml, thisVersion);
        try {
            //升级数据库
            executeCreateVersion(thisCreateVersion, true);
        } catch (Exception e) {
        }

    }

    /**
     * 开始升级
     *
     * @param context
     */
    public void startUpdateDb(Context context) {
        UpdateDbXml updateDbxml = readDbXml(context);
        if (getLocalVersionInfo()) {
            //拿到当前版本
            String currentVersion = getVersionName(context);
            //拿到上一个版本
            String lastVersion = this.lastVersion;
            UpdateStep updateStep = analyseUpdateStep(updateDbxml, lastVersion, currentVersion);

            if (updateStep == null) {
                return;
            }
            List<UpdateDb> updateDbs = updateStep.getUpdateDbs();
            CreateVersion createVersion = analyseCreateVersion(updateDbxml, currentVersion);

            try {
                //****************   把数据库都拷贝一份到backDb文件夹中  *********************
                backUpDb(updateDbs);
                //****************   把数据库都拷贝一份到backDb文件夹中  *********************

                //****************   将原有数据库重命名  *********************
                executeDb(updateDbs, -1);
                //****************   将原有数据库重命名  *********************

                //****************   创建新的数据库和表  *********************
                executeCreateVersion(createVersion, false);
                //****************   创建新的数据库和表  *********************

                //****************   将重命名后的数据库中的数据插入新建的数据库表中，然后删除这个重命名后的数据库
                executeDb(updateDbs, 1);
                //****************   将重命名后的数据库中的数据插入新建的数据库表中，然后删除这个重命名后的数据库
            } catch (Exception e) {
                e.printStackTrace();
                //发生异常，做一些处理
            }
            //****************   删除备份数据库（备份数据库是为了防止升级过程中出现问题而设）  *********************
            deleteBackupDb(updateDbs);
            //****************   删除备份数据库  *********************
        }
    }

    /**
     * 升级成功，删除备份
     *
     * @param updateDbs
     */
    private void deleteBackupDb(List<UpdateDb> updateDbs) {
        List<String> dbNames = getUpdateDbNames(updateDbs);
        if (dbNames.size() > 0) {
            for (String dbName : dbNames) {
                File userFileBak = new File(bakFile.getAbsolutePath() + File.separator + dbName + ".db");
                if (userFileBak.exists()) {
                    userFileBak.delete();
                }
            }
        }

    }

    /**
     * 备份原有数据库
     *
     * @param updateDbs 包含当前版本需要跟新的数据库信息，只需要将需要更新的数据库备份即可
     */
    private void backUpDb(List<UpdateDb> updateDbs) {

        List<String> dbNames = getUpdateDbNames(updateDbs);
        if (dbNames.size() > 0) {
            for (String dbName : dbNames) {
                String user = parentFile.getAbsolutePath() + File.separator + dbName + ".db";
                String user_bak = bakFile.getAbsolutePath() + File.separator + dbName + ".db";
                FileUtil.CopySingleFile(user, user_bak);
            }
        }
    }

    /**
     * 获取到所有需要备份的数据库名称，存入集合返回，一般在未分库的情况下，只有一个结果
     *
     * @param updateDbs
     * @return
     */
    private List<String> getUpdateDbNames(List<UpdateDb> updateDbs) {
        ArrayList<String> dbNames = new ArrayList<>();
        //获取所有需要备份的数据库名称并去除重复
        if (updateDbs != null) {
            for (UpdateDb updateDb : updateDbs) {
                String dbName = updateDb.getDbName();
                if (!TextUtils.isEmpty(dbName) && !dbNames.contains(dbName)) {
                    dbNames.add(dbName);
                }

            }
        }
        return dbNames;
    }

    /**
     * 根据建表脚本,核实一遍应该存在的表
     *
     * @param createVersion
     * @throws Exception
     */
    private void executeCreateVersion(CreateVersion createVersion, boolean isLogic) throws Exception {
        if (createVersion == null || createVersion.getCreateDbs() == null) {
            throw new Exception("check you updateXml.xml file to see if createVersion or createDbs node is null;");
        }

        for (CreateDb cd : createVersion.getCreateDbs()) {
            if (cd == null || cd.getName() == null) {
                throw new Exception("check you updateXml.xml file to see if createDb node or name is null");
            }
            // 创建数据库表sql
            List<String> sqls = cd.getSqlCreates();

            SQLiteDatabase sqlitedb = null;

            sqlitedb = getDb(cd.getName());
            executeSql(sqlitedb, sqls);
            sqlitedb.close();
        }
    }


    /**
     * 执行针对db升级的sql集合
     *
     * @param updateDbs 数据库操作脚本集合
     * @param type      小于0为建表前，大于0为建表后
     * @throws Exception
     * @throws throws    [违例类型] [违例说明]
     * @see
     */
    private void executeDb(List<UpdateDb> updateDbs, int type) throws Exception {
        if (updateDbs == null) {
            throw new Exception("updateDbs is null;");
        }
        for (UpdateDb db : updateDbs) {
            if (db == null || db.getDbName() == null) {
                throw new Exception("db or dbName is null;");
            }

            List<String> sqls = null;
            //更改表
            if (type < 0) {
                sqls = db.getSqlBefores();
            } else if (type > 0) {
                sqls = db.getSqlAfters();
            }

            SQLiteDatabase sqlitedb = null;

            sqlitedb = getDb(db.getDbName());

            executeSql(sqlitedb, sqls);

            sqlitedb.close();

        }
    }

    /**
     * 执行sql语句
     *
     * @param sqlitedb SQLiteDatabase
     * @param sqls     sql语句集合
     * @throws Exception 异常
     * @throws throws    [违例类型] [违例说明]
     * @see
     */
    private void executeSql(SQLiteDatabase sqlitedb, List<String> sqls) throws Exception {
        // 检查参数
        if (sqls == null || sqls.size() == 0) {
            return;
        }

        // 事务
        sqlitedb.beginTransaction();

        for (String sql : sqls) {
            sql = sql.replaceAll("\r\n", " ");
            sql = sql.replaceAll("\n", " ");
            if (!"".equals(sql.trim())) {
                try {
                    sqlitedb.execSQL(sql);
                } catch (SQLException e) {
                }
            }
        }

        sqlitedb.setTransactionSuccessful();
        sqlitedb.endTransaction();
    }


    /**
     * 新表插入数据
     *
     * @param xml
     * @param lastVersion 上个版本
     * @param thisVersion 当前版本
     * @return
     */
    private UpdateStep analyseUpdateStep(UpdateDbXml xml, String lastVersion, String thisVersion) {
        if (lastVersion == null || thisVersion == null) {
            return null;
        }

        // 更新脚本
        UpdateStep thisStep = null;
        if (xml == null) {
            return null;
        }
        List<UpdateStep> steps = xml.getUpdateSteps();
        if (steps == null || steps.size() == 0) {
            return null;
        }

        for (UpdateStep step : steps) {
            if (step.getVersionFrom() == null || step.getVersionTo() == null) {
            } else {
                // 升级来源以逗号分隔
                String[] lastVersionArray = step.getVersionFrom().split(",");

                if (lastVersionArray != null && lastVersionArray.length > 0) {
                    for (int i = 0; i < lastVersionArray.length; i++) {
                        // 有一个配到update节点即升级数据
                        if (lastVersion.equalsIgnoreCase(lastVersionArray[i]) && step.getVersionTo().equalsIgnoreCase(thisVersion)) {
                            thisStep = step;

                            break;
                        }
                    }
                }
            }
        }

        return thisStep;
    }

    /**
     * 创建数据库,获取数据库对应的SQLiteDatabase
     *
     * @param dbname
     * @return 设定文件
     * @throws throws [违例类型] [违例说明]sta
     * @see
     */
    private SQLiteDatabase getDb(String dbname) {
        String dbfilepath = null;
        SQLiteDatabase sqlitedb = null;
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        dbfilepath = parentFile.getAbsolutePath() + File.separator + dbname + ".db";// logic对应的数据库路径
        if (dbfilepath != null) {
            File f = new File(dbfilepath);
            f.mkdirs();
            if (f.isDirectory()) {
                f.delete();
            }
            sqlitedb = SQLiteDatabase.openOrCreateDatabase(dbfilepath, null);
        }

        return sqlitedb;
    }


    /**
     * 解析出对应版本的建表脚本
     *
     * @return
     */
    private CreateVersion analyseCreateVersion(UpdateDbXml xml, String version) {
        CreateVersion cv = null;
        if (xml == null || version == null) {
            return cv;
        }
        //获取到xml中createVersion节点对应的建表信息，这个信息是对应的最新版本的数据库信息
        //其他旧的版本数据库最终都要生成这个信息中指定的所有数据库和表的形式，这个节点可能会有
        //多个，以针对升级到不同版本的数据库升级处理，比如说，现在当前最新版本是10.0，目前市场
        //上6.0 7.0 8.0 9.0 都有相应的用户在使用，当前9.0的用户选择升级只能升级到最新的10.0
        //但是其他版本的用户就不同了，比如6.0用户，他有可能会选择升级到7.0 8.0 9.0 10.0中的
        //任意版本，由于每个版本的数据库结构可能都不相同，那么此时我们旧需要在xml中分别配置针对
        //7.0 8.0 9.0 10.0的四种升级方案，那么此时createVersion节点就存在四个，用户选择升
        //级到那个版本，就选择哪个createVersion节点进行处理
        //
        // 这就是createVersion[i].trim().equalsIgnoreCase(version)这一行判断存在的意义
        List<CreateVersion> createVersions = xml.getCreateVersions();
        if (createVersions != null) {
            for (CreateVersion item : createVersions) {
                //一般来说，一个createVersion设置一个version就可以了，但是有一些比较特殊的情况
                //比如8.0 和9.0 两个版本数据库并没有变化，所以升级到8.0 和9.0所进行的数据库升级
                //操作是一样的，所以可以把这两个版本号写在一块以,号隔开。或者你也可以分开写，复制
                //个一摸一样的节点，只把version值改一下，不过这样比较低级，不如写在一块节省代码
                String[] createVersion = item.getVersion().trim().split(",");

                for (int i = 0; i < createVersion.length; i++) {
                    //选择和当前升级后版本相匹配的数据库升级节点，version是升级后的版本
                    if (createVersion[i].trim().equalsIgnoreCase(version)) {
                        cv = item;

                        break;
                    }
                }
            }
        }

        return cv;
    }

    /**
     * 读取升级xml
     *
     * @param context
     * @return
     */
    private UpdateDbXml readDbXml(Context context) {
        InputStream is = null;
        Document document = null;
        try {
            is = context.getAssets().open(UPDATE_SCRIPT_XML);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException("read update script xml failed,check your asset dir to see if you have a script xml");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (document == null) {
            return null;
        }

        UpdateDbXml xml = new UpdateDbXml(document);

        return xml;
    }

    /**
     * 获取APK版本号
     *
     * @param context 上下文
     * @return 版本号
     * @throws throws [违例类型] [违例说明]
     * @see
     */
    public String getVersionName(Context context) {
        String versionName = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    /**
     * 这个方法是模拟一个情景，当前有一个新版本发布，此时运行于市场上的app通过版本更新
     * 接口请求到了新版本信息，新版本版本号为V003,当前版本版本号为V002,通过这个方法将旧版本
     * 号写入到一个文件中做记录，这个信息可以传递出此次升级是从哪个版本升级到哪个版本
     *
     * @return 保存成功返回true，否则返回false
     * @throws throws [违例类型] [违例说明]
     * @see
     */
    public boolean saveVersionInfo(String lastVersion) {
        boolean ret = false;

        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(parentFile, UPDATE_INFO), false);
            writer.write(lastVersion);
            writer.flush();
            ret = true;
        } catch (IOException e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * 获取本地版本相关信息,app升级之后需要保存升级之前的版本信息，从而
     * 根据这个信息可以知道，当前app是从哪个本版升级过来的
     *
     * @return 获取数据成功返回true，否则返回false
     * @throws throws [违例类型] [违例说明]
     * @see
     */
    private String lastVersion;

    private boolean getLocalVersionInfo() {
        boolean ret = false;

        File file = new File(parentFile, UPDATE_INFO);

        if (file.exists()) {
            int byteRead = 0;
            byte[] tempBytes = new byte[100];
            StringBuilder stringBuilder = new StringBuilder();
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                while ((byteRead = in.read(tempBytes)) != -1) {
                    stringBuilder.append(new String(tempBytes, 0, byteRead));
                }
                String infos = stringBuilder.toString();
                //lastVersion是当前版本升级之前的版本，当前版本是从这个版本升级过来的
                //这个信息是通过saveVersionInfo保存的
                lastVersion = infos;
                ret = true;
            } catch (Exception e) {

            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    in = null;
                }
            }
        }

        return ret;
    }
}
