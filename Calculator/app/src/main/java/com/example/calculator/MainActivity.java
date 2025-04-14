package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    String st="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void myfunction(View View)
    {

        /*Button b=(Button) findViewById(View.getId());
        EditText et=findViewById(R.id.ed1);
        String buttonText = b.getText().toString();
        switch (Integer.parseInt(b.getText().toString()))
        {
            case 1:
                st += "1";
                break;
            case 2:
                st += "2";
                break;
            case 3:
                st += "3";
                break;
            case 4:
                st += "4";
                break;
            case 5:
                st += "5";
                break;
            case 6:
                st += "6";
                break;
            case 7:
                st += "7";
                break;
            case 8:
                st += "8";
                break;
            case 9:
                st += "9";
                break;
            case 0:
                st += "0";
                break;
            default:
        }
        et.setText(st);
        if (buttonText.equals("+"))
        {
            st += "+";
        }
        else if (buttonText.equals("="))
        {
            String[] numbers = st.split("\\+");
            double sum = 0;
            for (String num : numbers)
            {
                if (!num.isEmpty())
                {
                    sum += Double.parseDouble(num);
                }
            }
            et.setText(String.valueOf(sum));
            st = String.valueOf(sum);
        }
        else if (buttonText.equals("C"))
        {
            st = "";
            et.setText("");
        } else
        {
            st += buttonText;
            et.setText(st);
        }*/


        /*Button b = (Button) findViewById(View.getId());
        EditText et = findViewById(R.id.ed1);
        String buttonText = b.getText().toString();
        if (buttonText.matches("\\d"))
        {
            st += buttonText;
        } else
        {
            switch (buttonText)
            {
                case "+":
                    st += "+";
                    break;
                case "=":
                    String[] numbers = st.split("\\+");
                    int sum = 0;
                    for (String num : numbers)
                    {
                        if (!num.isEmpty())
                        {
                            sum += Integer.parseInt(num);
                        }
                    }
                    et.setText(String.valueOf(sum));
                    st = String.valueOf(sum);
                    break;
                case "C":
                    st = "";
                    et.setText("");
                    break;
                default:
                    st += buttonText;
            }
        }
        et.setText(st);*/



        Button b = (Button) findViewById(View.getId());
        EditText et = findViewById(R.id.ed1);
        String buttonText = b.getText().toString();

        if (buttonText.matches("\\d")) {
            st += buttonText;
        } else {
            switch (buttonText) {
                case "+":
                case "-":
                case "*":
                case "/":
                    // If it's an operator, store the operator and the first number
                    st += buttonText;
                    break;
                case "=":
                    // Perform the calculation when "=" is pressed
                    calculateResult(et);
                    break;
                case "C":
                    // Clear the input
                    st = "";
                    et.setText("");
                    break;
            }
        }
        et.setText(st);
    }

        private void calculateResult(EditText et) {
        if (st.contains("+")) {
            String[] parts = st.split("\\+");
            if (parts.length == 2) {
                double num1 = Double.parseDouble(parts[0]);
                double num2 = Double.parseDouble(parts[1]);
                double result = num1 + num2;
                et.setText(String.valueOf(result));
                st = String.valueOf(result);
            }
        } else if (st.contains("-")) {
            String[] parts = st.split("-");
            if (parts.length == 2) {
                double num1 = Double.parseDouble(parts[0]);
                double num2 = Double.parseDouble(parts[1]);
                double result = num1 - num2;
                et.setText(String.valueOf(result));
                st = String.valueOf(result);
            }
        } else if (st.contains("*")) {
            String[] parts = st.split("\\*");
            if (parts.length == 2) {
                double num1 = Double.parseDouble(parts[0]);
                double num2 = Double.parseDouble(parts[1]);
                double result = num1 * num2;
                et.setText(String.valueOf(result));
                st = String.valueOf(result);
            }
        } else if (st.contains("/")) {
            String[] parts = st.split("/");
            if (parts.length == 2) {
                double num1 = Double.parseDouble(parts[0]);
                double num2 = Double.parseDouble(parts[1]);
                if (num2 != 0) {
                    double result = num1 / num2;
                    et.setText(String.valueOf(result));
                    st = String.valueOf(result);
                } else {
                    et.setText("Error");
                    st = "";
                }
            }
        }
    }
}
