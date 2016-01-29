package efis.e_sign;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.abk.ebics.xml.HVZResponseOrderData;


public class ListViewAdapter extends BaseAdapter {
    Context context;


    private static LayoutInflater inflater=null;
    ArrayList<Unterschrift> unterschriftArrayList;

    //Array mit allen UnterschriftenObjekten
//    Unterschrift [] unterschriftenArray;


    //wird von SignatureActivity übergeben
    boolean [] checkedItems;
    boolean bol = false;





    public ListViewAdapter (Context oldContext, ArrayList<Unterschrift> unterschriftenVonHVZ)
    {
        context = oldContext;
        unterschriftArrayList = unterschriftenVonHVZ;
        checkedItems = new boolean[unterschriftArrayList.size()];
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        notifyDataSetChanged();
    }

    //Array mit Selektierten/Unselektierten objekten wird übergeben, ListView wird aktulaisiert
    public void setSelectedItems(boolean [] itemsArray, boolean b){
        checkedItems = itemsArray;
        bol = b;
        notifyDataSetChanged();
    }




    @Override
    //Anzahl der Unterschriften
    public int getCount() {
        int i = unterschriftArrayList.size();

        return i;
    }


    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.customcell, null);

            holder viewholder = new holder();

            //configure View Holder
            viewholder.type = (TextView) rowView.findViewById(R.id.cellType);
            viewholder.records = (TextView) rowView.findViewById(R.id.cellRecords);
            viewholder.value = (TextView) rowView.findViewById(R.id.cellValue);
            viewholder.hash = (TextView) rowView.findViewById(R.id.cellHash);
            viewholder.creator = (TextView) rowView.findViewById(R.id.cellCreator);
            viewholder.createdDate = (TextView) rowView.findViewById(R.id.cellCreatorDate);
            viewholder.signator = (TextView) rowView.findViewById(R.id.cellSigner);
            viewholder.signatedDate = (TextView) rowView.findViewById(R.id.cellSignerDate);
            viewholder.checker = (ImageView) rowView.findViewById(R.id.checkerImageView);
            viewholder.rowtable = (TableRow) rowView.findViewById(R.id.celltablerow);

            rowView.setTag(viewholder);

        }


        // fill Data
        final holder viewHolder = (holder)rowView.getTag();
        viewHolder.value.setText(unterschriftArrayList.get(position).value);
        viewHolder.createdDate.setText(unterschriftArrayList.get(position).creatorDate);
        viewHolder.creator.setText(unterschriftArrayList.get(position).creator);
        viewHolder.hash.setText(unterschriftArrayList.get(position).hash);
        viewHolder.signatedDate.setText(unterschriftArrayList.get(position).signatorDate);
        viewHolder.signator.setText(unterschriftArrayList.get(position).signator);
        viewHolder.records.setText(unterschriftArrayList.get(position).records);
        viewHolder.type.setText(unterschriftArrayList.get(position).type);
        viewHolder.checker.setImageResource(R.drawable.arrowright);
        viewHolder.rowtable.setBackgroundColor(context.getResources().getColor(R.color.white));


            if(bol) {
                changeArrowtoChecker(viewHolder.checker, viewHolder.rowtable,
                        R.drawable.unchecked, context.getResources().getColor(R.color.white));
                notifyDataSetChanged();
                if (checkedItems[position]) {
                    changeArrowtoChecker(viewHolder.checker, viewHolder.rowtable, R.drawable.checked,
                            context.getResources().getColor(R.color.cellSelected));
                    notifyDataSetChanged();

                }else{
                    changeArrowtoChecker(viewHolder.checker, viewHolder.rowtable,
                            R.drawable.unchecked, context.getResources().getColor(R.color.white));
                    notifyDataSetChanged();
                }

            }else{
                changeArrowtoChecker(viewHolder.checker, viewHolder.rowtable, R.drawable.arrowright,
                        context.getResources().getColor(R.color.white));
                notifyDataSetChanged();

        }






        return rowView;
    }
    public  void changeArrowtoChecker(View imgageview, View tablerow,int drawabl, int color){
        ImageView vi = (ImageView)imgageview;
        TableRow t =(TableRow)tablerow;
        vi.setImageResource(drawabl);
        t.setBackgroundColor(color);


    }



    static class holder {
        public TextView value;
        public TextView hash;
        public TextView creator;
        public TextView createdDate;
        public TextView signator;
        public TextView signatedDate;
        public TextView type;
        public TextView records;
        public ImageView checker;
        public TableRow rowtable;

    }
}
