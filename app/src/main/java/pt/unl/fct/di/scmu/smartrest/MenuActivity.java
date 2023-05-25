package pt.unl.fct.di.scmu.smartrest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

import pt.unl.fct.di.scmu.smartrest.util.Item;
import pt.unl.fct.di.scmu.smartrest.util.MenuAdapter;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView menu;
    private Spinner menuDropDown;
    private MenuAdapter menuAdapter;
    private ImageButton goBackBtn;
    private ImageButton confirmBtn;

    private List<List<Item>> menuItems;
    private List<Item> entradas;
    private List<Item> sopas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menu = findViewById(R.id.menu_recycler_view);
        menuDropDown = findViewById(R.id.menu_spinner);
        goBackBtn = findViewById(R.id.menu_order_goback_btn);
        confirmBtn = findViewById(R.id.menu_order_confirm_btn);

        String[] items = new String[]{"Entradas", "Sopas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        menuDropDown.setAdapter(adapter);

        //alterar isto
        //async para ir buscar os items todos
        menuItems = new ArrayList<List<Item>>();

        Item item = new Item("pão", 0, 0.50f);
        Item item2 = new Item("queijo", 0, 1.00f);
        entradas = new ArrayList<Item>();
        entradas.add(item);
        entradas.add(item2);

        Item item3 = new Item("caldo-verde", 0, 2.00f);
        Item item4 = new Item("canja", 0, 2.00f);
        sopas = new ArrayList<Item>();
        sopas.add(item3);
        sopas.add(item4);

        menuAdapter = new MenuAdapter(this, entradas);

        menu.setAdapter(menuAdapter);
        menu.setLayoutManager(new LinearLayoutManager(this));

        menuDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                    {
                        menuAdapter = new MenuAdapter(MenuActivity.this, entradas);
                        menu.setAdapter(menuAdapter);
                        break;
                    }
                    case 1:
                    {
                        menuAdapter = new MenuAdapter(MenuActivity.this, sopas);
                        menu.setAdapter(menuAdapter);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResult(menuAdapter.getOrder(), getIntent().getIntExtra("table", -1));
            }
        });

    }

    private void returnResult(Map<String, Item> order, int table) {
        Intent returnIntent = new Intent();
        System.out.println("MENU_ACTIVITY - TABLE NUMBER " + table);
        returnIntent.putExtra("table", table);
        returnIntent.putExtra("result", (Serializable) order);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}