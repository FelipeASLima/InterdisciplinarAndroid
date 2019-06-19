package com.carona.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carona.R;
import com.carona.model.Carro;

import java.util.List;


public class AdapterCarro extends BaseAdapter {

    private final List<Carro> carros;

    private final Activity act;

    public AdapterCarro(List<Carro> carros, Activity act) {
        this.carros = carros;
        this.act = act;
    }

    @Override
    public int getCount() {
        return carros.size();
    }

    @Override
    public Object getItem(int position) {
        return carros.get(position);
    }

    @Override
    public long getItemId(int position) {
        return carros.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = act.getLayoutInflater()
                .inflate(R.layout.lista_carro, parent, false);


        Carro carro = carros.get(position);

        TextView marca = (TextView)
                view.findViewById(R.id.marca);
        TextView modelo = (TextView)
                view.findViewById(R.id.modelo);
        TextView placa = (TextView)
                view.findViewById(R.id.placa);
        TextView cor = (TextView)
                view.findViewById(R.id.cor);

        marca.setText(carro.getMarca());
        modelo.setText(carro.getModelo());
        placa.setText(carro.getPlaca());
        cor.setText(carro.getCor());

        return view;
    }
}
