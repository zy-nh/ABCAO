package com.example.frag.mytest;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static TanmuBean tanmuBean;
    private int validHeightSpace;
    private int index;
    private RelativeLayout container;
    private Handler handler;
    private client cli;
    private LocationManager locationmanager;
    private String locationprovider=null;
    public static int usersize=16;
    public static int usercolor=-16777216;
    public static boolean receive=true;
    public static ArrayList<String> list=new ArrayList<String>();
    public static storelist sl=new storelist("ABC.bak");
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        index=0;
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 0x123)
                {
                    if(receive==false)
                        return ;
                    String content = msg.obj.toString();
                    String[] lt=content.split(" ", 3);
                    float textSize =Integer.parseInt(lt[0]);
                    int textColor =Integer.parseInt(lt[1]);
                    for(int i=0;i<list.size();i++)
                    {
                        Pattern mypattern=Pattern.compile(list.get(i));
                        Matcher mymatcher=mypattern.matcher(lt[2]);
                        if(mymatcher.find())
                            return ;
                    }
                    showTanmu(lt[2], textSize, textColor);
                }
            }

        };
        cli=new client(handler);
        new Thread(cli).start();
        tanmuBean = new TanmuBean();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        locationmanager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> provider=locationmanager.getProviders(true);
        if(provider.contains(locationmanager.GPS_PROVIDER))
            locationprovider=locationmanager.GPS_PROVIDER;
        else if(provider.contains(locationmanager.NETWORK_PROVIDER))
            locationprovider=locationmanager.NETWORK_PROVIDER;
        else
        {
            Toast.makeText(this, "没有可用的位置提供器，请打开GPS或网络", Toast.LENGTH_SHORT).show();
            return;
        }
        Location location=null;
        while(location==null)
            location=locationmanager.getLastKnownLocation(locationprovider);
        //ChangePosition(location);

        //list.add("fdsfsd");
        locationmanager.requestLocationUpdates(locationprovider, 3000, 1, locationlistener);
        //sl.write(list);
        list=sl.read();
    }

    private void ChangePosition(Location location)
    {
        try {
            Message msg = new Message();
            msg.what = 0x345;
            msg.obj = "ChanGe PoSition " + location.getLatitude() + " " + location.getLongitude();
            cli.myhandler.sendMessage(msg);
        }catch(NullPointerException e)
        {
            Toast.makeText(MainActivity.this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
        }
    }

    LocationListener locationlistener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            ChangePosition(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this, "没有可用的位置提供器，请打开GPS或网络", Toast.LENGTH_SHORT).show();
        }

    };
    public void onClick(View v)
    {
        Button button=(Button)v.findViewById(R.id.button);
        Message msg=new Message();
        msg.what=0x345;
        EditText edittext=(EditText)findViewById(R.id.editText);
        msg.obj=String.valueOf(usersize)+" "+String.valueOf(usercolor)+" "+edittext.getText().toString();
        if(!edittext.getText().toString().equals(""))
        {
            cli.myhandler.sendMessage(msg);
            edittext.setText("");
        }
    }

    public void onClick2(View v)
    {
        EditText myedittext=(EditText)(findViewById(R.id.editText3));
        String lt=myedittext.getText().toString();
        myedittext.setText("");
        for(int i=0;i<list.size();i++)
            if(lt.equals(list.get(i)))
            {
                Toast.makeText(this, "表达式已存在", Toast.LENGTH_SHORT).show();
                return ;
            }
        if(!lt.equals(""))
            PlaceholderFragment.adapter.add(lt);
        else
            Toast.makeText(this, "表达式不能为空", Toast.LENGTH_SHORT).show();
    }
    private void showTanmu(String content, float textSize, int textColor) {
        container = (RelativeLayout) findViewById(R.id.relativeLayout);
        final TextView textView = new TextView(this);
        textView.setTextSize(textSize);
        textView.setText(content);
//        textView.setSingleLine();
        textView.setTextColor(textColor);

        int leftMargin = container.getRight() - container.getLeft() - container.getPaddingLeft();
        //计算本条弹幕的topMargin(随机值，但是与屏幕中已有的不重复)
        int verticalMargin = getRandomTopMargin();
        textView.setTag(verticalMargin);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = verticalMargin;
        textView.setLayoutParams(params);
        Animation anim = AnimationHelper.createTranslateAnim(this, leftMargin, -ScreenUtils.getScreenWidth(this));
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //移除该组件
                container.removeView(textView);
                //移除占位
                int verticalMargin = (int) textView.getTag();
                existMarginValues.remove(verticalMargin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textView.startAnimation(anim);

        container.addView(textView);
    }
    private Set<Integer> existMarginValues = new HashSet<>();
    private int linesCount;

    private int getRandomTopMargin() {
        //计算用于弹幕显示的空间高度
        if (validHeightSpace == 0) {
            validHeightSpace = container.getBottom() - container.getTop()
                    - container.getPaddingTop() - container.getPaddingBottom();
        }

        //计算可用的行数
        if (linesCount == 0) {
            linesCount = validHeightSpace / DensityUtils.dp2px(this, tanmuBean.getMinTextSize() * (1 + tanmuBean.getRange()));
            if (linesCount == 0) {
                throw new RuntimeException("Not enough space to show text.");
            }
        }

        //检查重叠
        while (true) {
            int randomIndex = (int) (Math.random() * linesCount);
            int marginValue = randomIndex * (validHeightSpace / linesCount);

            if (!existMarginValues.contains(marginValue)) {
                existMarginValues.add(marginValue);
                return marginValue;
            }
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        static private View v1=null;
        static private View v2=null;
        static private View v3=null;
        static private View v4=null;
        static public ArrayAdapter<String> adapter;



        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int sectionNumber=getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView;
            if(sectionNumber==2)
            {
                if(v2==null) {
                    v2 = inflater.inflate(R.layout.fragment_blank, container, false);
                    SeekBar myseekbar = (SeekBar) (v2.findViewById(R.id.seekBar));
                    myseekbar.setOnSeekBarChangeListener(seekbarlistener);
                    Spinner myspinner = (Spinner) (v2.findViewById(R.id.spinner));
                    myspinner.setOnItemSelectedListener(spinnerlistener);
                    Switch myswitch = (Switch) (v2.findViewById(R.id.switch1));
                    myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                MainActivity.receive = true;
                            } else {
                                MainActivity.receive = false;
                            }
                        }
                    });
                }
                rootView = v2;
            }
            else if(sectionNumber==3) {
                if(v3==null) {
                    v3 = inflater.inflate(R.layout.shield, container, false);
                    MyListView myListView = (MyListView) (v3.findViewById(R.id.MyListView));
                    myListView.setRemoveListener(new MyListView.RemoveListener() {
                        @Override
                        public void removeItem(MyListView.RemoveDirection direction, int position) {
                            adapter.remove(adapter.getItem(position));
                            sl.write(list);
                        }
                    });
                    adapter = new ArrayAdapter<String>(this.getContext(), R.layout.item, R.id.list_item, list);
                    myListView.setAdapter(adapter);
                    myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                        }
                    });

                }
                rootView = v3;
            }
            else if(sectionNumber==4)
            {
                if(v4==null)
                    v4=inflater.inflate(R.layout.fragment_help, container, false);
                rootView = v4;
            }
            else
            {
                    v1=inflater.inflate(R.layout.fragment_main, container, false);
                rootView = v1;
            }
            return rootView;
        }
        private Spinner.OnItemSelectedListener spinnerlistener=new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner lt = (Spinner) parent;
                MainActivity.usercolor=tanmuBean.getColor(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        private SeekBar.OnSeekBarChangeListener seekbarlistener=new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser==true)
                    MainActivity.usersize=progress+16;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
