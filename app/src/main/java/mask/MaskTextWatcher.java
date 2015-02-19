package mask;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;


public class MaskTextWatcher implements TextWatcher {
    //////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE MEMBERS
    //////////////////////////////////////////////////////////////////////////////////////

    private MaskFormatter mMaskFormatter;

    //////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////

    public MaskTextWatcher(String mask) {
        mMaskFormatter = new MaskFormatter(mask);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // IMPLEMENTATION
    //////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void afterTextChanged(Editable s) {
        String filtered = mMaskFormatter.valueToString(s);

        if (!TextUtils.equals(s, filtered)) {
            s.replace(0, s.length(), filtered);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public String getMask() {
        return mMaskFormatter.getMask();
    }

    public void setMask(String mask) {
        mMaskFormatter.setMask(mask);
    }
}