package ca.brocku.calculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.mariuszgromada.math.mxparser.*;

// Uses Framework v5.0.3, which may be included with JRE 8, and JDK 8, and
// OpenJDK 8.
// This project uses mXparser installed for JDK 8!
// 1) install mXparser, copy and paste mXparser version for JDK 8 into Project>app>libs
// 2) right click on mXparser file, and click add as Library

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private boolean formulaMode;


    private double operand1;
    private String operator;
    // booleans to check if the required parts of an operation have been entered
    private boolean operand1_entered;
    private boolean operator_entered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.Display);
        formulaMode=false;
        operand1_entered=false;
        operator_entered=false;
        // get saved states so we can switch from portrait to landscape
        if(savedInstanceState != null){
            operand1 = savedInstanceState.getDouble("operand1");
            operator = savedInstanceState.getString("operator");
            operand1_entered = savedInstanceState.getBoolean("operand1_entered");
            operator_entered = savedInstanceState.getBoolean("operator_entered");
            display.setText(savedInstanceState.getString("display"));
            formulaMode = savedInstanceState.getBoolean("formulaMode");
        }
    }
    //  update the calculator screen    //
    public void updateText(String newString) {
        String old = display.getText().toString();
        display.setText(String.format("%s%s",old,newString));
    }
    public void evaluate(String the_operator){
        if(operand1_entered){ //has the first operator been entered?
            if(operator_entered){ //has a previous operator been entered?
                    // check if there's actually a valid second operand ...
                    if(!display.getText().toString().equals("")&&!display.getText().toString().equals("+")&&!display.getText().toString().equals("−")&&!display.getText().toString().equals("×")&&!display.getText().toString().equals("÷")){
                        double operand2 = Double.parseDouble(display.getText().toString());
                        // evaluate the set of operand1, operator and operand2 and make the result the new operand1
                        double result=0;
                        switch (operator){
                            case "+":
                                result = operand1 + operand2;
                                break;
                            case "−":
                                result = operand1 - operand2;
                                break;
                            case "×":
                                result = operand1 * operand2;
                                break;
                            case "÷":
                                result = operand1 / operand2;
                                break;
                        }
                        operand1 = result;
                        // check for error
                        if(result==Double.POSITIVE_INFINITY||result==Double.NEGATIVE_INFINITY|| Double.isNaN(result)){
                            // display error message
                            String error = "error";
                            display.setText(error);

                            operand1_entered=false;
                            operator_entered=false;
                        }
                        else if(the_operator.equals("=")){ //display answer, and make it the new first operand for further calculations
                            operator_entered=false;
                            display.setText(String.valueOf(operand1));
                        }
                        else {
                            operator = the_operator;
                            display.setText(the_operator);
                            operator_entered = true;
                        }
                    }
                    // else if a previous operator was entered, replace it with the new operator
                    else if(display.getText().toString().equals("+")||display.getText().toString().equals("−")||display.getText().toString().equals("×")||display.getText().toString().equals("÷")){
                        operator = the_operator;
                        display.setText(the_operator);
                    }
                }
            else{ // no existing operator? okay add this one then.
                // UNLESS you entered an equal sign
                if(!the_operator.equals("=")){
                    operator = the_operator;
                    operator_entered = true;
                    display.setText(the_operator);
                }
            }
        }
        else{ //try and get first operand
            // check if there's a VALID first operand
            if(!display.getText().toString().equals("")&&!display.getText().toString().equals("+")&&!display.getText().toString().equals("−")&&!display.getText().toString().equals("×")&&!display.getText().toString().equals("÷")&&!display.getText().toString().equals("=")&&!display.getText().toString().equals("error")){
                operand1 = Double.parseDouble(display.getText().toString());
                operand1_entered=true;
                if(!the_operator.equals("=")){
                    operator=the_operator;
                    display.setText(the_operator);
                    operator_entered=true;
                }
            }
            // if no first operator, then don't do anything
        }
    }
    public void removeOperator(){ //remove operator from display if new number is entered
        if(display.getText().toString().equals("+")||display.getText().toString().equals("−")||display.getText().toString().equals("×")||display.getText().toString().equals("÷")){
            display.setText("");
        }
    }
    public void removeError(){ // remove error message when a new button is pressed
        if(display.getText().toString().equals("error"))
            display.setText("");
    }

    //  all the button on-click handlers    //

    //  function buttons/non-number buttons //
    public void toggleFormulaMode(View button){
        formulaMode=!formulaMode;
    }
    public void allClear(View button) {
        display.setText("");
        operand1_entered=false;
        operator_entered=false;
    }
    public void clear(View button) {
        display.setText("");
    }
    public void add(View button) {
        removeError();
        if(formulaMode){ // if formula mode is enabled, display operators on the display
            updateText("+");
        }
        else{
            evaluate("+");
        }
    }
    public void subtract(View button) {
        removeError();
        if(formulaMode){
            updateText("−");
        }
        else{
            evaluate("−");
        }
    }
    public void multiply(View button) {
        removeError();
        if(formulaMode){
            updateText("×");
        }
        else{
            evaluate("×");
        }
    }
    public void divide(View button) {
        removeError();
        if(formulaMode){
            updateText("÷");
        }
        else{
            evaluate("÷");
        }
    }
    public void decimal(View button) {
        removeError();
        removeOperator();
        if(formulaMode){
            updateText(".");
        }
        else{
            // if a decimal point has not been entered yet...
            if(!display.getText().toString().contains(".")){
                // if the display is blank, append a 0 AND the decimal to avoid math errors if you say, try to type
                // .5 instead of 0.5 (it'll fix your mistake so the app doesn't crash)
                if(display.getText().toString().equals("")){
                    display.setText(new StringBuilder(display.getText().toString()).append("0"));
                }
                display.setText(new StringBuilder(display.getText().toString()).append("."));
            }
        }
    }
    public void equals(View button) {
        removeError();
        if(formulaMode){
            String displayExp = display.getText().toString(); //expression in the display.
            // Allow mXparser to read expression string by replacing the
            // divide and multiply symbols with "/" and "*" respectively.
            displayExp = displayExp.replaceAll("÷","/");
            displayExp = displayExp.replaceAll("×","*");
            displayExp = displayExp.replaceAll("−","-");
            Expression expression = new Expression(displayExp);
            String result = String.valueOf(expression.calculate());
            if(result.equals("NaN")){
                result="error"; // i want it to display "error" instead lol
            }

            display.setText(result);
        }
        else{
            evaluate("=");
        }
    }

    //  number buttons  //
    public void zero(View button) {
        removeError();
        removeOperator();
        updateText("0");
    }
    public void one(View button) {
        removeError();
        removeOperator();
        updateText("1");
    }
    public void two(View button) {
        removeError();
        removeOperator();
        updateText("2");
    }
    public void three(View button) {
        removeError();
        removeOperator();
        updateText("3");
    }
    public void four(View button) {
        removeError();
        removeOperator();
        updateText("4");
    }
    public void five(View button) {
        removeError();
        removeOperator();
        updateText("5");
    }
    public void six(View button) {
        removeError();
        removeOperator();
        updateText("6");
    }
    public void seven(View button) {
        removeError();
        removeOperator();
        updateText("7");
    }
    public void eight(View button) {
        removeError();
        removeOperator();
        updateText("8");
    }
    public void nine(View button) {
        removeError();
        removeOperator();
        updateText("9");
    }

    // save states so we can switch from portrait to landscape
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("operand1", operand1);
        outState.putBoolean("operand1_entered", operand1_entered);
        outState.putBoolean("operator_entered", operator_entered);
        outState.putString("operator", operator);
        outState.putString("display", display.getText().toString());
        outState.putBoolean("formulaMode", formulaMode);
    }
}