package pe.applica.gasolima.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pe.applica.gasolima.GasStationListActivity;
import pe.applica.gasolima.ItemChoiceManager;
import pe.applica.gasolima.R;
import pe.applica.gasolima.data.StationsContract;

/**
 * Adapter to be used in the Station List Activity
 * Created by jhoon on 1/11/16.
 */
public class StationsAdapter
        extends RecyclerView.Adapter<StationsAdapter.ViewHolder> {

    private Cursor mCursor;
    private final Context mContext;
    private final StationOnClickHandler mClickHandler;
    private final ItemChoiceManager mICM;

    public StationsAdapter(Context context, StationOnClickHandler clickHandler, int choiceMode) {
        mContext = context;
        mClickHandler = clickHandler;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_gasstation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.mNameView.setText(mCursor.getString(GasStationListActivity.COL_STATION_NAME));
        holder.mGasesView.setText(mCursor.getString(GasStationListActivity.COL_STATION_GASES));
        holder.mDistanceView.setText(mCursor.getString(GasStationListActivity.COL_STATION_DISTANCE));

        mICM.onBindViewHolder(holder, position);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof ViewHolder) {
            ViewHolder svf = (ViewHolder) viewHolder;
            svf.onClick(svf.itemView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;
        @Bind(R.id.station_icon) public ImageView mIconView;
        @Bind(R.id.station_name_textview) public TextView mNameView;
        @Bind(R.id.station_gases_textview) public TextView mGasesView;
        @Bind(R.id.station_distance_textview) public TextView mDistanceView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumnIndex = mCursor.getColumnIndex(StationsContract.StationEntry._ID);
            mClickHandler.onClick(mCursor.getInt(idColumnIndex), this);
            mICM.onClick(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    public interface StationOnClickHandler {
        void onClick(int id, ViewHolder vh);
    }
}
