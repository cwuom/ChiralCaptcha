package com.cwuom.chiralcaptcha.activity;

import static com.cwuom.chiralcaptcha.statics.Constants.MAX_MOLECULES;
import static com.cwuom.chiralcaptcha.util.AppConfig.addAverageDuration;
import static com.cwuom.chiralcaptcha.util.AppConfig.addFileIndex;
import static com.cwuom.chiralcaptcha.util.AppConfig.addNotPassedCount;
import static com.cwuom.chiralcaptcha.util.AppConfig.addPassedCount;
import static com.cwuom.chiralcaptcha.util.AppConfig.getFileIndex;
import static com.cwuom.chiralcaptcha.util.AppConfig.subFileIndex;
import static com.cwuom.chiralcaptcha.util.Utils.snackbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cwuom.chiralcaptcha.Adapter.HistoryAdapter;
import com.cwuom.chiralcaptcha.BuildConfig;
import com.cwuom.chiralcaptcha.Dao.HistoryDao;
import com.cwuom.chiralcaptcha.Entity.EntityHistory;
import com.cwuom.chiralcaptcha.InitDataBase.InitHistoryDataBase;
import com.cwuom.chiralcaptcha.R;
import com.cwuom.chiralcaptcha.chiral.carbon.ChiralCarbonHelper;
import com.cwuom.chiralcaptcha.chiral.carbon.MdlMolParser;
import com.cwuom.chiralcaptcha.chiral.carbon.Molecule;
import com.cwuom.chiralcaptcha.chiral.carbon.MoleculeView;
import com.cwuom.chiralcaptcha.databinding.ActivityChiralCaptchaBinding;
import com.cwuom.chiralcaptcha.statics.Constants;
import com.cwuom.chiralcaptcha.util.AppConfig;
import com.cwuom.chiralcaptcha.util.DarkModeViewModel;
import com.cwuom.chiralcaptcha.util.Logger;
import com.cwuom.chiralcaptcha.util.RandomUtil;
import com.cwuom.chiralcaptcha.util.Utils;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.dialogs.FullScreenDialog;
import com.kongzue.dialogx.interfaces.OnBindView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressLint("StaticFieldLeak")
public class ChiralCaptchaActivity extends AppCompatActivity {
    private static WeakReference<Context> ctx; // week ctx
    private static ActivityChiralCaptchaBinding binding;
    private static DarkModeViewModel darkModeViewModel;
    private String currentMoleculeStr = null;
    private static int cMoleculePoolIndex = 1;
    private String cMoleculePath = "";
    private static String cFilename;
    private static HashSet<Integer> chiralCarbons = null;
    private static Molecule mol; // current molecule
    private CountDownTimer countDownTimer;
    private long baseWaitTime = 600000;
    private long timeLeftInMillis = -1;
    private long startTimeMillis = -1;
    private boolean isCheating = false;
    private boolean isTimeout = false;
    private boolean isStartedAnswer = false;
    private static Boolean passed = false;
    private static String cElapsedTimeFormatted = "";
    private int error = 0;
    private int checkCount = 0;
    private long ctime; // create time
    private HistoryDao historyDao;
    private EntityHistory entityHistory;

    long elapsedTimeMillis;

    private static boolean doNotCheckCheating = false;
    private static boolean isLimitCCQuantity = false;
    private static boolean isHideCCQuantity = false;
    private static boolean dynamicAnswerTimeEnabled = false;
    private static boolean isSequentialLoadMode = false;
    private static int maxCCGeneratedQuantity = 0;
    private static int minCcGeneratedQuantity = 0;
    private static int preloadCount = 0;
    private static int maxViewCount = 0;
    private static int maxStorageCount = 0;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChiralCaptchaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ctx = new WeakReference<>(this);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());

        readAppConfig(false);

        if (savedInstanceState != null) {
            resetCfg();
            currentMoleculeStr = savedInstanceState.getString("currentMoleculeStr");
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis");
            isCheating = savedInstanceState.getBoolean("isCheating");
            isStartedAnswer = savedInstanceState.getBoolean("isStartedAnswer");
            passed = savedInstanceState.getBoolean("passed");
            cElapsedTimeFormatted = savedInstanceState.getString("cElapsedTimeFormatted");
            error = savedInstanceState.getInt("error");
            isTimeout = savedInstanceState.getBoolean("isTimeout");
            checkCount = savedInstanceState.getInt("checkCount");
            cMoleculePoolIndex = savedInstanceState.getInt("cMoleculePoolIndex");
            cMoleculePath = savedInstanceState.getString("cMoleculePath");
            cFilename = savedInstanceState.getString("fileName");
            ctime = savedInstanceState.getLong("ctime");

            if (isStartedAnswer){
                if (passed){
                    onPassed();
                } else {
                    startTimer();
                }
            }
            init(true);
        } else {
            init(false);
        }

    }

    void readAppConfig(boolean toastErr) {
        baseWaitTime = AppConfig.getAnswerTimeBase(this) * 1000L;
        doNotCheckCheating = AppConfig.isDisableScoreCheck(this);
        isLimitCCQuantity = AppConfig.isLimitQuantity(this);
        isHideCCQuantity = AppConfig.isHideQuantity(this);
        dynamicAnswerTimeEnabled = AppConfig.isDynamicAnswerTime(this);
        maxCCGeneratedQuantity = AppConfig.getMaxQuantity(this);
        minCcGeneratedQuantity = AppConfig.getMinQuantity(this);
        preloadCount = AppConfig.getPreloadCount(this);
        maxStorageCount = AppConfig.getMaxStorageCount(this);
        maxViewCount = AppConfig.getMaxViewCount(this);
        cMoleculePoolIndex = AppConfig.getMolPoolIndex(this);
        isSequentialLoadMode = AppConfig.isSequentialLoadMode(this);

        if (isLimitCCQuantity){
            var ref = new Object() {
                String err = "";
            };
            if (maxCCGeneratedQuantity - minCcGeneratedQuantity <= 1){
                isLimitCCQuantity = false;
                ref.err = "请配置生成数量的范围，它太小了";
            }
            if (cMoleculePoolIndex > 2 && isLimitCCQuantity){
                isLimitCCQuantity = false;
                ref.err = "无法限制生成数量。分子数据库必须为L1或L2";

            }

            if (!isLimitCCQuantity && toastErr) {
                snackbar(getAppContext(), ref.err);
            }
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(boolean recreate) {
        // init database
        InitHistoryDataBase initHistoryDataBase = Utils.getInstance(this);
        historyDao = initHistoryDataBase.historyDao();
        entityHistory = new EntityHistory();

        // Initialize ViewModel and observe dark mode changes
        darkModeViewModel = new ViewModelProvider(this).get(DarkModeViewModel.class);
        darkModeViewModel.observeDarkModeChanges(this);

        setSupportActionBar(binding.topAppBar);

        darkModeViewModel.getDarkModeState().observe(this, isDarkMode -> MoleculeView.initTextColor());

        new Thread(() -> {
            if (!recreate) {
                // Generate and load molecule
                structuralUpdater(null, false);
            } else {
                try {
                    // load molecule
                    mol = MdlMolParser.parseString(currentMoleculeStr);
                    chiralCarbons = ChiralCarbonHelper.getMoleculeChiralCarbons(mol);

                    runOnUiThread(() -> {
                        // Set molecule to the view
                        binding.moleculeView.setMolecule(mol);
                        hideLoadingIndicator();
                        topBarSubTitleUpdater();
                    });
                } catch (MdlMolParser.BadMolFormatException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }).start();



        // change the molecule
        binding.btnNextMolecule.setOnClickListener(v -> new Thread(() -> structuralUpdater(null, false)).start());
        binding.btnPreviousMolecule.setOnClickListener(v -> {
            EntityHistory e = historyDao.getPreviousHistoryEntry();
            if (e != null) {
                historyDao.deleteHistoryByCTime(ctime);
                mol = getMolecule(e.getCMolPath());
                structuralUpdater(mol, true);
                historyDao.deleteHistoryByCTime(e.getCtime());
            } else {
                snackbar(getAppContext(), "没有找到上一题的记录");
            }
        });

        // check the answer
        binding.btnCheck.setOnClickListener(v -> {
            checkCount++;
            passed = true;
            HashSet<Integer> tmp = new HashSet<>(chiralCarbons);
            for (int i : binding.moleculeView.getSelectedChiral()) {
                if (tmp.contains(i)) {
                    tmp.remove(i);
                } else {
                    passed = false;
                    error++;
                }
            }
            if (!tmp.isEmpty()) {
                error += tmp.size();
                passed = false;
            }

            if (passed) {
                onPassed();
                return;
            }
            if (binding.moleculeView.getSelectedChiral().length == 0){
                snackbar(getAppContext(),"请先选择");
                checkCount--;
            } else {
                snackbar(getAppContext(),"检查到"+error+"处错误");
                addNotPassedCount(getAppContext(), 1);
            }
            error = 0;
        });

        binding.btnStart.setOnClickListener(v -> startTimer());

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    void onPassed(){
        binding.btnStart.setClickable(false);
        binding.btnCheck.setVisibility(View.GONE);
        binding.btnNextMolecule.setText("Next");
        snackbar(getAppContext(), "验证通过！");
        binding.moleculeView.setOnTouchListener((v1, event) -> true);
        calculateElapsedTime();
        if (isCheating) {
            binding.btnStart.setText("查看答案后此项无效");
            binding.btnStart.setTextColor(getColor(R.color.orangered));
        } else {
            addPassedCount(getAppContext(), 1);
            addAverageDuration(getAppContext(), elapsedTimeMillis);
        }
        try {
            updateEntityHistory();
        } catch (Exception e) {
            Logger.e("Cannot update history: " + e.getMessage());
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public void structuralUpdater(Molecule mol2, boolean prev) {
        readAppConfig(true);
        historyDao.deleteOlderThanX(maxStorageCount);

        if (prev){
            binding.btnNextMolecule.setVisibility(View.VISIBLE);
        }

        try {
            stopTimer();
            if (mol2 == null) {
                if (mol != null && !passed && isSequentialLoadMode && getFileIndex(getAppContext()) < getMaxMolIdForSelectedValue(cMoleculePoolIndex)) {
                    updateEntityHistory();
                }
                Utils.runOnUiThread(this::showLoadingIndicator);
                if (!isSequentialLoadMode){
                    int result = randomMolecule();
                    if (result != 1){
                        Utils.runOnUiThread(() -> binding.topAppBar.setSubtitle("载入超时，请检查生成范围"));

                        if (result == 3){
                            Utils.runOnUiThread(() -> binding.topAppBar.setSubtitle("出现未知错误"));
                        }

                        Utils.runOnUiThread(() -> binding.topAppBar.setSubtitleTextColor(getColor(R.color.orangered)));
                        Utils.runOnUiThread(this::hideLoadingIndicator);

                        return;
                    }
                } else {
                    addFileIndex(getAppContext(), cMoleculePoolIndex);
                    int file_index = getFileIndex(getAppContext());
                    mol = getMolecule(String.format("mol_%s/", cMoleculePoolIndex) + file_index + ".mol");
                }

            } else {
                mol = mol2;
            }

            if (prev && isSequentialLoadMode){
                subFileIndex(getAppContext());
            }

            runOnUiThread(() -> {
                chiralCarbons = ChiralCarbonHelper.getMoleculeChiralCarbons(mol);
                hideLoadingIndicator();

                binding.moleculeView.setMolecule(mol);
                topBarSubTitleUpdater();

                binding.btnCheck.setVisibility(View.VISIBLE);
                binding.moleculeView.setOnTouchListener((v, event) -> {
                    if (!isStartedAnswer) {
                        startTimer();
                    }

                    binding.moleculeView.onTouchEvent(event);


                    return true;
                });

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }

                resetCfg();
                Logger.e(String.valueOf(getFileIndex(getAppContext())));
                Logger.e(String.valueOf(getFileIndex(getAppContext())));
                createEntityHistory();
                if (getFileIndex(getAppContext()) == getMaxMolIdForSelectedValue(cMoleculePoolIndex)){
                    binding.btnNextMolecule.setVisibility(View.GONE);
                }

            });

        } catch (Exception e) {
            Logger.e("Cannot load molecule: " + e);
        }

    }

    public void showLoadingIndicator(){
        binding.tvLoading.setVisibility(View.VISIBLE);
        binding.tvLoading.setText("载入中..");
        binding.moleculeView.setVisibility(View.GONE);
        binding.btnStart.setClickable(false);
        binding.btnCheck.setClickable(false);
        binding.btnPreviousMolecule.setClickable(false);
        binding.btnNextMolecule.setClickable(false);
    }

    public void hideLoadingIndicator(){
        binding.tvLoading.setVisibility(View.GONE);
        binding.moleculeView.setVisibility(View.VISIBLE);
        binding.btnStart.setClickable(true);
        binding.btnCheck.setClickable(true);
        binding.btnPreviousMolecule.setClickable(true);
        binding.btnNextMolecule.setClickable(true);
    }

    public static void topBarSubTitleUpdater(){
        int c = binding.moleculeView.getSelectedChiralCount();
        binding.topAppBar.setSubtitleTextColor(getAppContext().getColor(R.color.text_sub_color));
        if (!isHideCCQuantity){
            binding.topAppBar.setSubtitle("L" + (cMoleculePoolIndex) + " | " + cFilename + " | " +
                    c + "/" + chiralCarbons.size());
            if (c > chiralCarbons.size()){
                binding.topAppBar.setSubtitleTextColor(getAppContext().getColor(R.color.orangered));
            }
        } else {
            binding.topAppBar.setSubtitle("L" + (cMoleculePoolIndex) +" | " + cFilename);
        }

    }



    @SuppressLint("SetTextI18n")
    void resetCfg() {
        stopTimer();
        binding.btnStart.setText("开始作答");
        binding.btnStart.setClickable(true);
        timeLeftInMillis = baseWaitTime;
        if (dynamicAnswerTimeEnabled){
            timeLeftInMillis = (long) (baseWaitTime * (1+0.1*chiralCarbons.size()));
        }
        isCheating = false;
        isStartedAnswer = false;
        passed = false;
        cElapsedTimeFormatted = "";
        error = 0;
        isTimeout = false;
        binding.btnStart.setTextColor(getColor(R.color.text_sub_color));
        checkCount = 0;
        binding.btnNextMolecule.setText("Skip");
    }

    private void updateEntityHistory() {
        historyDao.deleteHistoryByCTime(ctime);
        entityHistory.setCMolPath(cMoleculePath);
        entityHistory.setCheating(isCheating ? 1 : 0);
        entityHistory.setPassed(passed ? 1 : 0);
        entityHistory.setFinishTime(!Objects.equals(cElapsedTimeFormatted, "") ? cElapsedTimeFormatted : "N/A");
        if (startTimeMillis == -1){
            entityHistory.setFinishTime("N/A");
        }
        entityHistory.setCFileName(cFilename);
        entityHistory.setChiralCarbonsCount(chiralCarbons.size());
        entityHistory.setCMoleculePoolIndex(cMoleculePoolIndex);
        entityHistory.setCheckCount(checkCount);
        entityHistory.setIsTimeout(isTimeout ? 1 : 0);
        entityHistory.setCtime(ctime);
        historyDao.insertHistory(entityHistory);
    }

    private void createEntityHistory() {
        ctime = System.currentTimeMillis();
        entityHistory.setCMolPath(cMoleculePath);
        entityHistory.setCheating(-1);
        entityHistory.setPassed(-1);
        entityHistory.setFinishTime("N/A");
        entityHistory.setCFileName(cFilename);
        entityHistory.setChiralCarbonsCount(chiralCarbons.size());
        entityHistory.setCMoleculePoolIndex(cMoleculePoolIndex);
        entityHistory.setCheckCount(0);
        entityHistory.setIsTimeout(-1);
        entityHistory.setCtime(ctime);
        historyDao.insertHistory(entityHistory);
    }


    private int randomMolecule() {
        Future<?> future = executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                cMoleculePoolIndex = RandomUtil.randint(1, 4);
                cFilename = RandomUtil.randint(1, MAX_MOLECULES[cMoleculePoolIndex - 1]) + ".mol";
                try {
                    AssetManager am = getAppContext().getAssets();
                    cMoleculePath = String.format("mol_%s/", cMoleculePoolIndex) + cFilename;
                    InputStream is = am.open(cMoleculePath);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                    StringBuilder molStr = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        molStr.append(line).append("\n");
                    }
                    bufferedReader.close();
                    is.close();

                    currentMoleculeStr = molStr.toString();
                    mol = MdlMolParser.parseString(molStr.toString());
                    if (!isLimitCCQuantity){
                        return;
                    }
                    int size = ChiralCarbonHelper.getMoleculeChiralCarbons(mol).size();
                    if (size > minCcGeneratedQuantity && size <= maxCCGeneratedQuantity){
                        return;
                    }

                    mol = null;
                } catch (IOException | MdlMolParser.BadMolFormatException e) {
                    throw new IllegalStateException("Error reading or parsing the mol file: " + cFilename + "\n" + e);
                }
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
            return 1;
        } catch (TimeoutException e) {
            future.cancel(true);
            return 2;
        } catch (Exception e) {
            return 3;
        }


    }

    private Molecule getMolecule(String path) {
        try {
            AssetManager am = getAppContext().getAssets();
            cMoleculePath = path;
            cFilename = cMoleculePath.split("/")[1];

            InputStream is = am.open(cMoleculePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder molStr = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                molStr.append(line).append("\n");
            }
            bufferedReader.close();
            is.close();

            currentMoleculeStr = molStr.toString();
            return MdlMolParser.parseString(molStr.toString());
        } catch (IOException | MdlMolParser.BadMolFormatException e) {
            throw new IllegalStateException("Error reading or parsing the mol file: " + cFilename + "\n" + e);
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentMoleculeStr", currentMoleculeStr);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
        outState.putBoolean("isCheating", isCheating);
        outState.putBoolean("isStartedAnswer", isStartedAnswer);
        outState.putBoolean("passed", passed);
        outState.putString("cElapsedTimeFormatted", cElapsedTimeFormatted);
        outState.putInt("error", error);
        outState.putBoolean("isTimeout", isTimeout);
        outState.putInt("checkCount", checkCount);
        outState.putInt("cMoleculePoolIndex", cMoleculePoolIndex);
        outState.putString("cMoleculePath", cMoleculePath);
        outState.putString("fileName", cFilename);
        outState.putSerializable("chiralCarbons", chiralCarbons);
        outState.putLong("ctime", ctime);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove observers when the activity is destroyed
        darkModeViewModel.getDarkModeState().removeObservers(this);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        ctx.clear();
        ctx = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_setting) {
            startActivity(new Intent(getAppContext(), SettingActivity.class));
        } else if (item.getItemId() == R.id.item_history_manager) {   // view the history
            showHistoryManager();
        } else if (item.getItemId() == R.id.item_display_answer) {  // view the reference answer
            binding.moleculeView.displayAnswer(chiralCarbons);
            binding.btnStart.setClickable(false);
            if (!doNotCheckCheating){
                isCheating = true;
            }
        } else if (item.getItemId() == R.id.item_about) {  // about
            String versionName = BuildConfig.VERSION_NAME;
            int padding = 50;
            TextView tv = new TextView(this);
            tv.setText(R.string.app_description);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setPadding(padding, padding, padding, padding);

            new MaterialAlertDialogBuilder(this)
                    .setTitle("ChiralCaptcha\n"+versionName)
                    .setView(tv)
                    .show();
        } else if (item.getItemId() == R.id.item_clear_answer) {  // unselected all chiral
            binding.moleculeView.unselectedAllChiral();
        } else if (item.getItemId() == R.id.item_mol_selector) {  // show the molecule setter
            showMoleculeSelector();
        }
        return true;
    }



    // -------------------------------------------------------------------------------

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calculateElapsedTime() {
        long endTimeMillis = System.currentTimeMillis();
        elapsedTimeMillis = endTimeMillis - startTimeMillis;
        int elapsedSeconds = (int) (elapsedTimeMillis / 1000);
        cElapsedTimeFormatted = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60);
        binding.btnStart.setText("用时: " + cElapsedTimeFormatted + " | 验证次数: " + checkCount);
        binding.btnStart.setTextColor(getColor(R.color.forestgreen));
        stopTimer();
    }

    private void stopTimer(){
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startTimer() {
        stopTimer();

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
            @Override
            public void onFinish() {
                updateTimer();

                binding.btnCheck.setVisibility(View.GONE);
                snackbar(getAppContext(), "您已超时，答案已放出");
                binding.btnStart.setText("回答超时");
                binding.btnStart.setTextColor(getColor(R.color.orangered));
                binding.moleculeView.displayAnswer(chiralCarbons);
                binding.btnNextMolecule.setText("Next");
                binding.moleculeView.setOnTouchListener((v1, event) -> true);
                isTimeout = true;
            }
        }.start();

        startTimeMillis = System.currentTimeMillis();
    }

    @SuppressLint("SetTextI18n")
    private void updateTimer() {
        isStartedAnswer = true;
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        @SuppressLint("DefaultLocale") String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        binding.btnStart.setText(timeFormatted);
        binding.btnStart.setClickable(false);
    }


    void showMoleculeSelector(){
        BottomDialog.show("", "",
            new OnBindView<>(R.layout.layout_mol_selector) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onBind(BottomDialog dialog12, View v) {
                    v.findViewById(R.id.btn_go_back).setOnClickListener(v1 -> dialog12.dismiss());
                    dialog12.setBackgroundColor(getColor(R.color.background));
                    AutoCompleteTextView actv = v.findViewById(R.id.actv_pool_index);
                    TextInputEditText molId = v.findViewById(R.id.mol_id);

                    String[] poolIndex = {"L1 (5000 molecules)", "L2 (4999 molecules)", "L3 (1000 molecules)", "L4 (373 molecules)"};
                    actv.setText(poolIndex[0], true);
                    actv.setAdapter(new ArrayAdapter<>(getAppContext(), R.layout.custom_dropdown_item, poolIndex));


                    final int[] selectedValue = {1};
                    actv.setOnItemClickListener((parent, view, position, id) -> {
                        selectedValue[0] = position + 1;
                        Log.d("SelectedPoolIndex", "Selected value: " + selectedValue[0]);
                    });

                    v.findViewById(R.id.go).setOnClickListener(v1 -> {
                        if (Objects.requireNonNull(molId.getText()).toString().isEmpty()) {
                            snackbar(getAppContext(), "请输入题目编号");
                            return;
                        }

                        try {
                            int molIdInt = Integer.parseInt(molId.getText().toString());
                            int maxMolId = getMaxMolIdForSelectedValue(selectedValue[0]);

                            if (molIdInt < 1 || molIdInt > maxMolId) {
                                snackbar(getAppContext(), "题目编号应在1~" + maxMolId + "之间");
                            } else {
                                changeMolecules(selectedValue[0], molIdInt + ".mol");
                                dialog12.dismiss();
                            }
                        } catch (NumberFormatException e) {
                            snackbar(getAppContext(), "请输入整数");
                        }
                    });
                }
            });


    }

    public int getMaxMolIdForSelectedValue(int selectedValue) {
        return switch (selectedValue) {
            case 2 -> Constants.MAX_MOLECULES_L2;
            case 3 -> Constants.MAX_MOLECULES_L3;
            case 4 -> Constants.MAX_MOLECULES_L4;
            default -> Constants.MAX_MOLECULES_L1;
        };
    }



    void showHistoryManager(){
        FullScreenDialog.show(new OnBindView<>(R.layout.layout_history) {
            @Override
            public void onBind(FullScreenDialog dialog, View v) {
                dialog.setBackgroundColor(getColor(R.color.background));
                setupHistoryManagerUI(dialog, v);
            }
        }).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    void setupHistoryManagerUI(FullScreenDialog dialog, View v){
        int load_size = preloadCount;  // pre loaded history count
        AtomicInteger load_limit = new AtomicInteger();

        load_limit.addAndGet(load_size);

        v.findViewById(R.id.btn_go_back).setOnClickListener(v1 -> dialog.dismiss());
        TextView tv_no_card = v.findViewById(R.id.no_card);
        LinearLayout action_bar = v.findViewById(R.id.history_action_bar);
        RecyclerView rv_card_list = v.findViewById(R.id.card_list);
        TextView tv_count = v.findViewById(R.id.number_of_cards);
        TextView tv_more_options = v.findViewById(R.id.more_options);
        List<EntityHistory> localCard = historyDao.loadHistoryEntries(0, load_limit.get());

        ScrollView scrollViewHistory = v.findViewById(R.id.scrollView_history);

        action_bar.setOnLongClickListener(v13 -> scrollViewHistory.post(() -> scrollViewHistory.smoothScrollTo(0, 0)));

        if (localCard == null || localCard.isEmpty()) {
            tv_count.setText("");
            tv_no_card.setVisibility(View.VISIBLE);
        } else {
            tv_count.setText(getString(R.string.number_of_cards, Objects.requireNonNull(localCard).size()+""));
            HistoryAdapter cardHistoryAdapter = new HistoryAdapter((ArrayList<EntityHistory>) localCard, this, count -> {
                if (count <= 0) {
                    tv_count.setText("");
                    tv_no_card.setVisibility(View.VISIBLE);
                } else {
                    tv_count.setText(getString(R.string.number_of_cards, count + ""));
                }

            }, (action, entity, pos) -> doEntityHistoryAction(action, entity, pos, rv_card_list, dialog), maxViewCount);

            rv_card_list.setAdapter(cardHistoryAdapter);
            rv_card_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });

            scrollViewHistory.setOnScrollChangeListener((v12, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                int scrollRange = scrollViewHistory.getChildAt(0).getHeight() - scrollViewHistory.getHeight();
                int threshold = (int) (scrollRange * 0.75);

                if (scrollY >= threshold) {
                    load_limit.addAndGet(load_size);
                    List<EntityHistory> cards = historyDao.loadHistoryEntries(0, load_limit.get());
                    cardHistoryAdapter.submitList(cards);
                }
            });

        }

        tv_more_options.setOnClickListener(v14 -> {
            PopupMenu popupMenu = new PopupMenu(this, v14);
            popupMenu.getMenuInflater().inflate(R.menu.history_manage_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.item_delete_history) {
                    new MaterialAlertDialogBuilder(v14.getContext())
                            .setTitle("确认删除所有卡片么？")
                            .setMessage("此操作不可逆。")
                            .setNeutralButton("手滑了..", null)
                            .setPositiveButton("确认删除", (dialog1, which) -> {
                                historyDao.deleteHistory();
                                tv_count.setText("");
                                rv_card_list.setAdapter(null);
                                tv_no_card.setVisibility(View.VISIBLE);
                            })
                            .show();
                }
                if (id == R.id.item_help){
                    new MaterialAlertDialogBuilder(v14.getContext())
                            .setTitle("历史管理器说明")
                            .setMessage( "采用懒加载，下滑会自动更新\n注: 至多显示"
                                    +maxViewCount+"条记录, "+maxStorageCount+"条往后的记录会被自动删除\n长按上方操作栏可快速滚动到顶部")
                            .show();
                }
                return false;
            });
            popupMenu.show();
        });
    }



    void doEntityHistoryAction(int action, EntityHistory e, int pos, RecyclerView rv_card_list, FullScreenDialog dialog) {
        switch (action) {
            case Constants.ACTION_REANSWER:
                changeMolecules(e.getCMoleculePoolIndex(), e.getCFileName());
                dialog.dismiss();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }
    }


    void changeMolecules(int pl_index, String filename){
        new Thread(() -> {
            cMoleculePoolIndex = pl_index;
            cFilename = filename;

            mol = getMolecule(String.format("mol_%s/", cMoleculePoolIndex) + cFilename);
            structuralUpdater(mol, false);

            runOnUiThread(() -> {
                binding.moleculeView.setMolecule(mol);
                hideLoadingIndicator();
                topBarSubTitleUpdater();
            });
        }).start();
    }

    public static Context getAppContext() {
        return Objects.requireNonNull(ctx != null ? ctx.get() : null, "AppContext is null!");
    }
}