package pt.unl.fct.di.scmu.smartrest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.scmu.smartrest.util.Item;

public class TableMapActivity extends AppCompatActivity {

    private static final String TAG = "TableMapActivity";
    private Toolbar toolbar;
    private Button addTableBtn;
    private RelativeLayout currentRow;
    private Button addItemBtn;
    private Button paymentBtn;
    private ImageButton closeFormBtn;

    private List<HashMap<String,Item>> tables;

    private int tableCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_map);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        addTableBtn = findViewById(R.id.add_table_btn);
        addTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTable();
            }
        });

        addItemBtn = findViewById(R.id.add_item_btn);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOrder(v);
            }
        });

        paymentBtn = findViewById(R.id.payment_btn);
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(v);
            }
        });

        closeFormBtn = findViewById(R.id.close_table_orders_form_btn);
        closeFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout orderForm = findViewById(R.id.table_orders_layout);
                orderForm.setVisibility(View.GONE);
            }
        });

        tables = new ArrayList<HashMap<String,Item>>();

        tableCounter = 0;

    }

    private void pay(View v) {
        //TODO: async enviar recibo para base de dados
    }

    private void newOrder(View v) {
        TextView formTitle = findViewById(R.id.layout_table_id);
        String[] split = formTitle.getText().toString().split(" ");
        String number = split[1];
        int table = Integer.valueOf(number);
        Log.e(TAG,"newOrder: " + "NUMBER " + table);
        startForResult(this,1, table);
    }

    private void newTable() {

        final Button table = new Button(this);
        table.setText(tableCounter + "");
        table.setTextColor(getColor(R.color.white));
        table.setId(tableCounter);
        table.setBackground(getDrawable(R.drawable.rounded_button));

        RelativeLayout.LayoutParams paramsBtn = new RelativeLayout.LayoutParams(250, 250);

        switch(tableCounter % 3){
            case 0 :
                paramsBtn.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 1 :
                paramsBtn.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 2 :
                paramsBtn.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
        }
        table.setLayoutParams(paramsBtn);

        table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTableInfo(v.getId());
            }
        });

        if(tableCounter % 3 == 0){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,100);

            currentRow = new RelativeLayout(this);
            currentRow.setLayoutParams(params);
            currentRow.addView(table);

            LinearLayout tableContainer = findViewById(R.id.table_list);
            tableContainer.addView(currentRow);

        }else{
            currentRow.addView(table);
        }
        tableCounter++;

        tables.add(new HashMap<String, Item>());

    }

    private void showTableInfo(int table) {

        HashMap<String,Item> tableItems = (HashMap<String, Item>) tables.get(table);
        LinearLayout tableList = findViewById(R.id.table_item_list);
        tableList.removeAllViews();

        TextView formTitle = findViewById(R.id.layout_table_id);
        formTitle.setText("Table " + table);

        Log.e(TAG, "showTableInfo: " + formTitle.getText());

        RelativeLayout orderForm = findViewById(R.id.table_orders_layout);
        orderForm.setVisibility(View.VISIBLE);

        if(!tableItems.isEmpty()) {

            Iterator it = tableItems.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Item> pair = (Map.Entry<String, Item>) it.next();
                Item i = pair.getValue();

                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                TextView itemName = new TextView(this);
                itemName.setLayoutParams(params1);
                itemName.setText(i.getItemName());

                TextView itemQuantity = new TextView(this);
                itemQuantity.setLayoutParams(params2);
                itemQuantity.setText(i.getItemQuantity());

                RelativeLayout item = new RelativeLayout(this);
                item.addView(itemName);
                item.addView(itemQuantity);

                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeQuantity(v);
                    }
                });

                tableList.addView(item);
            }
        }

    }

    private void changeQuantity(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_logout: {
                logout();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static void startForResult(Activity activity, int requestCode, int table) {
        Intent intent = new Intent(activity, MenuActivity.class);
        intent.putExtra("table", table);
        activity.startActivityForResult(intent, requestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                HashMap<String, Item> order = (HashMap<String, Item>) data.getSerializableExtra("order");
                int table = data.getIntExtra("table", -1);
                System.out.println("TABLE " + table + " " +  "MAP " + order);
                if(table != -1)
                    updateOrder(order, table);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateOrder(HashMap<String, Item> order, int table){

        Iterator it = order.entrySet().iterator();
        HashMap<String, Item> tableCurrentOrder = tables.get(table);
        while(it.hasNext()){
            Map.Entry<String,Item> pair = (Map.Entry<String,Item>)it.next();

            String itemId = pair.getKey();
            Item item = pair.getValue();

            if(tableCurrentOrder.containsKey(itemId)){
                tableCurrentOrder.get(itemId).add();
            }else{
                tableCurrentOrder.put(itemId, item);
            }

            showTableInfo(table);
        }

    }

    private void logout() {
        //logout;
        finish();
    }
}