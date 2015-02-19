package mask;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by hifenhur on 14/02/15.
 */
public class MoneyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public MoneyTextWatcher(EditText editText) {
        editTextWeakReference = new WeakReference<EditText>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        EditText editTex = editTextWeakReference.get();
        if(!s.toString().equals(editTex.getText())){
            editTex.removeTextChangedListener(this);

            String cleanString = s.toString().replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString.replaceAll("[^\\d]", ""));
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

            editTex.setText(formatted);
            editTex.setSelection(formatted.length());

            editTex.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
//        EditText editText = editTextWeakReference.get();
//        if (editText == null) return;
//        String s = editable.toString();
//        editText.removeTextChangedListener(this);
//        String cleanString = s.toString().replaceAll("[$,.]", "");
//        BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
//        String formatted = NumberFormat.getCurrencyInstance().format(parsed);
//        editText.setText(formatted);
//        editText.setSelection(formatted.length());
//        editText.addTextChangedListener(this);
    }
}