import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class App extends Application {
    private TextField displayField;

    public static void main(String[] args) {
        launch(App.class, args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculator");
        GridPane gridPane = createGridPane();
        addDisplayField(gridPane);
        addButtons(gridPane);
        Scene scene = new Scene(gridPane, 300.0, 400.0);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10.0));
        gridPane.setHgap(5.0);
        gridPane.setVgap(5.0);
        return gridPane;
    }

    private void addDisplayField(GridPane gridPane) {
        displayField = new TextField();
        displayField.setPrefHeight(70.0);
        displayField.setEditable(false);
        gridPane.add(displayField, 0, 0, 5, 1);
    }

    private void addButtons(GridPane gridPane) {
        String[][] buttonLabels = new String[][]{
                {"asin", "acos", "atan", "π", "del"},
                {"tan", "cot", "√", "^", "!"},
                {"7", "8", "9", "/", "c"},
                {"4", "5", "6", "*", "ln"},
                {"1", "2", "3", "-", "sin"},
                {"0", ".", "=", "+", "cos"}
        };

        for (int row = 0; row < buttonLabels.length; ++row) {
            for (int col = 0; col < buttonLabels[row].length; ++col) {
                Button button = createButton(buttonLabels[row][col]);
                gridPane.add(button, col, row + 1);
            }
        }
    }

    private Button createButton(String label) {
        Button button = new Button(label);
        button.setPrefWidth(70.0);
        button.setPrefHeight(70.0);
        button.setOnAction((e) -> handleButtonClick(label));
        return button;
    }

    private void handleButtonClick(String label) {
        if (isSpecialFunction(label)) {
            evaluateSpecialFunction(label);
        } else if (label.equals("=")) {
            evaluateExpression();
        } else if (label.equals("c")) {
            displayField.clear();
        }else if (label.equals("π")) {
            displayField.setText(Double.toString(Math.PI));
        }else if (label.equals("del")) {
            String currentText = displayField.getText();
            if (!currentText.isEmpty()) {
                String newText = currentText.substring(0, currentText.length() - 1);
                displayField.setText(newText);
            }
        }
        else {
            displayField.appendText(label);
        }
    }

    private boolean isSpecialFunction(String label) {
        return label.equals("sin") || label.equals("cos") || label.equals("tan") ||
                label.equals("cot") || label.equals("asin") || label.equals("acos") ||
                label.equals("atan") || label.equals("ln");
    }

    private void evaluateSpecialFunction(String label) {
        String expression = displayField.getText();
        if (expression.isEmpty()) {
            displayField.setText("Invalid expression");
            return;
        }

        double result;

        switch (label) {
            case "sin" -> result = Math.sin(Math.toRadians(Double.parseDouble(expression)));
            case "cos" -> result = Math.cos(Math.toRadians(Double.parseDouble(expression)));
            case "tan" -> result = Math.tan(Math.toRadians(Double.parseDouble(expression)));
            case "cot" -> result = 1 / Math.tan(Math.toRadians(Double.parseDouble(expression)));
            case "asin" -> result = Math.toDegrees(Math.asin(Double.parseDouble(expression)));
            case "acos" -> result = Math.toDegrees(Math.acos(Double.parseDouble(expression)));
            case "atan" -> result = Math.toDegrees(Math.atan(Double.parseDouble(expression)));
            case "π" -> result = Math.PI;
            case "ln" -> result = Math.log(Double.parseDouble(expression));
            default -> {
                displayField.setText("Invalid function");
                return;
            }
        }

        displayField.setText(Double.toString(result));
    }

    private void evaluateExpression() {
        String expression = displayField.getText();
        if (expression.isEmpty()) {
            displayField.setText("Invalid expression");
            return;
        }

        // Use regular expression to split the expression into tokens
        String[] tokens = expression.split("(?<=[-+*/^√!])|(?=[-+*/^√!])");

        double result;
        String operator = "";  // Initialize the operator variable

        // Determine the operator based on the tokens
        for (String token : tokens) {
            if (isNumeric(token)) {
                continue; // Skip numbers
            } else if (isFunction(token) || token.equals("π")) {
                operator = token;
                break;
            } else if (isOperator(token)) {
                operator = token;
                break;
            } else {
                displayField.setText("Invalid operator");
                return;
            }
        }

        // Handle unary operators
        if (isUnaryOperator(operator)) {
            if (tokens.length != 2) {
                displayField.setText("Invalid expression");
                return;
            }


            double operand = Double.parseDouble(tokens[1]);

            switch (operator) {
                case "√":
                    result = Math.sqrt(operand);
                    break;
                case "sin":
                    result = Math.sin(Math.toRadians(operand));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(operand));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(operand));
                    break;
                case "cot":
                    result = 1 / Math.tan(Math.toRadians(operand));
                    break;
                case "asin":
                    result = Math.toDegrees(Math.asin(operand));
                    break;
                case "acos":
                    result = Math.toDegrees(Math.acos(operand));
                    break;
                case "atan":
                    result = Math.toDegrees(Math.atan(operand));
                    break;
                case "!":
                    int number = (int) operand;
                    if (number < 0 || number != Math.floor(number)) {
                        displayField.setText("Invalid expression");
                        return;
                    }
                    result = calculateFactorial(number);
                    break;
                default:
                    displayField.setText("Invalid operator");
                    return;
            }

        } else {
            // Handle binary operators
            if (tokens.length != 3) {
                displayField.setText("Invalid expression");
                return;
            }

            double num1 = Double.parseDouble(tokens[0]);
            double num2 = Double.parseDouble(tokens[2]);

            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0.0) {
                        displayField.setText("Invalid expression");
                        return;
                    }
                    result = num1 / num2;
                    break;
                case "^":
                    result = Math.pow(num1, num2);
                    break;
                default:
                    displayField.setText("Invalid operator");
                    return;
            }
        }

        displayField.setText(Double.toString(result));
    }

    private boolean isUnaryOperator(String operator) {
        return operator.equals("√") || operator.equals("sin") || operator.equals("cos") ||
                operator.equals("tan") || operator.equals("cot") || operator.equals("asin") ||
                operator.equals("acos") || operator.equals("atan") || operator.equals("!");
    }

    private boolean isNumeric(String token) {
        return Pattern.matches("-?\\d+(\\.\\d+)?", token);
    }

    private boolean isOperator(String token) {
        return token.matches("[-+*/^]");
    }

    private boolean isFunction(String token) {
        return token.matches("√|sin|cos|tan|cot|asin|acos|atan|!");
    }

    private double calculateFactorial(int number) {
        if (number == 0 || number == 1) {
            return 1;
        } else {
            return number * calculateFactorial(number - 1);
        }
    }
}