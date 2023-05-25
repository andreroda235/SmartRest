package pt.unl.fct.di.scmu.smartrest.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.scmu.smartrest.R;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<Item> itemList;
    private Context context;
    Map<String, Item> order;

    public MenuAdapter(Context context, List<Item> itemList){
        this.context = context;
        this.itemList = itemList;
        order = new HashMap<>();
    }

    public Map<String, Item> getOrder(){
        return order;
    }


    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.menu_row, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {

        Item item = itemList.get(position);

        holder.itemName.setText(item.getItemName());
        holder.itemPrice.setText(String.valueOf(item.getItemPrice()) + "€");
        System.out.println("HERE " + item.getItemQuantity());
        holder.itemQuant.setText(String.valueOf(item.getItemQuantity()));

        holder.add.setTag(position);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup row = ((ViewGroup)v.getParent());
                String itemName = ((TextView) row.findViewById(R.id.item_name_textView)).getText().toString();
                int pos = (Integer) v.getTag();
                Item item = itemList.get(pos);

                if(item.getItemQuantity() == 0 && !order.containsKey(itemName)) {
                    order.put(itemName, item);
                }
                item.add();

                TextView quantityText = row.findViewById(R.id.item_quantity_textView);
                quantityText.setText(String.valueOf(item.getItemQuantity()));
            }
        });

        holder.remove.setTag(position);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup row = ((ViewGroup)v.getParent());
                String itemName = ((TextView) row.findViewById(R.id.item_name_textView)).getText().toString();
                int pos = (Integer) v.getTag();

                Item item = itemList.get(pos);

                if(item.getItemQuantity() == 1)
                    order.remove(itemName);
                item.remove();

                TextView quantityText = row.findViewById(R.id.item_quantity_textView);
                quantityText.setText(String.valueOf(item.getItemQuantity()));

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName, itemPrice, itemQuant;
        private Button add, remove;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.item_name_textView);
            itemPrice = itemView.findViewById(R.id.item_price_textView);
            itemQuant = itemView.findViewById(R.id.item_quantity_textView);

            add = itemView.findViewById(R.id.item_quantity_add);
            remove = itemView.findViewById(R.id.item_quantity_remove);

        }
    }
}
