package com.example.cookbook.ui.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiSelectionSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private List<String> items;
    private boolean[] selected;
    private String defaultText;
    private MultiSpinnerListener listener;

    public MultiSelectionSpinner(Context context) {
        super(context);
    }

    public MultiSelectionSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSelectionSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        refreshText();
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                (dialog, which) -> dialog.cancel());
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(List<String> items, String allText,
                         MultiSpinnerListener listener) {
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;

        // all selected by default
        selected = new boolean[items.size()];
        for (int i = 0; i < selected.length; i++)
            selected[i] = false;

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{allText});

        setAdapter(adapter);
        refreshText();
    }

    public void setItemsWithMap(List<ItemWithCheckedOptions> items, String allText, MultiSpinnerListener listener) {
        this.items = items.stream().map(i -> i.name).collect(Collectors.toList());
        this.defaultText = allText;
        this.listener = listener;

        selected = new boolean[items.size()];
        for (int i = 0; i < items.size(); i++)
            selected[i] = items.get(i).checked;

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{allText});

        setAdapter(adapter);
        refreshText();
    }

    public List<String> getSelected() {
        List<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                selectedItems.add(items.get(i));
            }
        }

        return selectedItems;
    }

    private void refreshText() {
        StringBuffer spinnerBuffer = new StringBuffer();
        boolean someSelected = false;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append(", ");
                someSelected = true;
            }
        }
        String spinnerText;
        if (someSelected) {
            spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        } else {
            spinnerText = defaultText;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{spinnerText});
        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);
    }

    public static class ItemWithCheckedOptions {
        public String name;
        public Boolean checked;

        public ItemWithCheckedOptions(String name, Boolean checked) {
            this.name = name;
            this.checked = checked;
        }
    }
}
