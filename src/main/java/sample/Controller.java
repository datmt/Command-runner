package sample;

import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.io.IOUtils;
import sample.helpers.UserPrefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Controller {


    @FXML
    TextArea commandsTA, errorOutputTA, outputTA;

    @FXML
    ListView commandsLV, commandGroupLV;

    @FXML
    TextField newGroupNameTF;






    ObservableList<String> commandGroupList = FXCollections.observableArrayList();

    HashMap<String, String> commandsMap = new HashMap<String, String>();


    @FXML
    public void initialize()
    {

        commandGroupLV.setItems(commandGroupList);

        populateCommandMap();
        populateCommandList();
        prepareCommandGroupLV();


    }

    private void populateCommandMap()
    {
        String commandsString = UserPrefs.getCommands();

        if (commandsString.equals(""))
            return;


        Gson g = new Gson();
        commandsMap = g.fromJson(commandsString, HashMap.class);
    }

    private void populateCommandList()
    {

        Iterator it = commandsMap.entrySet().iterator();

        //populate the list of commands first
        while (it.hasNext())
        {
            Map.Entry<String, String> entry = (Map.Entry) it.next();
            commandGroupList.add(entry.getKey());
        }


    }



    private void clearOutputs()
    {
        errorOutputTA.clear();
        outputTA.clear();
    }


    public void runSelectedCommand()
    {
        clearOutputs();
        //get the commands from the hashmap and run it
        String selectedCommandGroup = commandGroupLV.getSelectionModel().getSelectedItem().toString();
        String[] commands = commandsMap.get(selectedCommandGroup).trim().split("\n");

        ProcessBuilder processBuilder = new ProcessBuilder();

        String os = System.getProperty("os.name");

        for (String command : commands)
        {
            String[] commandArray;

            if (command.trim().equals("") || command.startsWith("--"))
                continue;
            if (os.equals("Windows"))
            {
                commandArray = new String[]{"cmd", "/c", command };

            } else
            {
                commandArray = new String[]{"sh", "-c", command };
            }

            try
            {
                processBuilder.command(commandArray);
                Process p = processBuilder.start();
                p.waitFor();

                String outputText = IOUtils.toString(p.getInputStream(), Charset.defaultCharset());
                String errorTExt = IOUtils.toString(p.getErrorStream(), Charset.defaultCharset());

                outputTA.setText(outputText);
                errorOutputTA.setText(errorTExt);


                System.out.println("---------- COMMAND OUTPUT ----------");

                System.out.println(outputText);

                System.out.println("---------- END COMMAND OUTPUT ----------");



                System.out.println("---------- ERROR OUTPUT ----------");

                System.out.println(errorTExt);

                System.out.println("---------- END ERROR OUTPUT ----------");
            } catch (Exception e)
            {
                e.printStackTrace();
            }


        }





    }


    /**
     * 1. First, save the list of command groups
     * 2. Save the list of commands
     */
    public void saveCommands()
    {

        //if no command group is selected, just don't do anything
        if (commandGroupLV.getSelectionModel().getSelectedItem() == null)
            return;
        //get the selected command in the list and update its value
        String selectedCommand = commandGroupLV.getSelectionModel().getSelectedItem().toString();
        System.out.println("selected command group is: " + selectedCommand);

        String allCommands = commandsTA.getText().trim();

        commandsMap.put(selectedCommand, allCommands);
    }


    //Make the command list view populate the commadns when an item is clicked
    private void prepareCommandGroupLV()
    {

        commandGroupLV.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                String selectedCommandGroup = commandGroupLV.getSelectionModel().getSelectedItem().toString();
                String commands = commandsMap.get(selectedCommandGroup);

                commandsTA.setText(commands);
            }
        });
    }

    public void addNewCommandGroup()
    {
        String newCommandGroup = newGroupNameTF.getText().trim();
        newGroupNameTF.clear();

        commandGroupList.add(newCommandGroup);
        commandsMap.put(newCommandGroup, "");

        updateCommandsString();
    }

    public void deleteCommandGroup()
    {
        int selectIndex = commandGroupLV.getSelectionModel().getSelectedIndex();
        String selectedCommand = commandGroupLV.getSelectionModel().getSelectedItem().toString();
        commandGroupLV.getItems().remove(selectIndex);
        //remove from the hash
        commandsMap.remove(selectedCommand);

        updateCommandsString();

    }

    //update the commands in user prefs
    private void updateCommandsString()
    {
        Gson g = new Gson();
        UserPrefs.setCommands(g.toJson(commandsMap));
    }



}
