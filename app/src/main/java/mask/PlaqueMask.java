package mask;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

public class PlaqueMask
{
    public static TextWatcher insert(final EditText ediTxt)
    {

        return new TextWatcher() {
            boolean isUpdating;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mascara="";
                String text=s.toString().replace("-", "").replace(",", "").replace(".", "");
                if (isUpdating)
                {

                    isUpdating = false;
                    return;
                }
                if(text.length()==7)
                {
                    mascara=s.toString();
                }
                else{

                    if(text.length()<3)
                    {
                        ediTxt.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                        mascara=text.toString();
                    }
                    else if(text.length()==3)
                    {
                        ediTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mascara=text;
                    }
                    else
                    {
                        ediTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mascara=text.toString().substring(0, 3)+"-"+text.toString().substring(3);
                    }
                }
                isUpdating = true;

                ediTxt.setText(mascara.toUpperCase());
                ediTxt.setSelection(mascara.length());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        };
    }

}
