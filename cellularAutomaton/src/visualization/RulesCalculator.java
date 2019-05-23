package visualization;

import javafx.collections.ObservableList;

public class RulesCalculator {

    private int y1;
    private int y2;

    private void periodicCondition(int j, int n) {
        if(j == 0)
            y1 = n-1;
        else
            y1 = j-1;
        if(j == n-1)
            y2 = 0;
        else
            y2 = j+1;
    }
    private ObservableList<ObservableList<Integer>> calculateByRule30(ObservableList<ObservableList<Integer>> array) {
        for(int i = 1; i < array.size(); i++)
        {
            for(int j = 0; j < array.get(0).size(); j++)
            {
                periodicCondition(j,array.get(0).size());
                if((array.get(i-1).get(y1) != array.get(i-1).get(y2) && array.get(i-1).get(j) ==0)
                    || (array.get(i-1).get(y1) ==0 &&  array.get(i-1).get(y2)==1 && array.get(i-1).get(j) == 1)
                    || (array.get(i-1).get(y1) == 0 && array.get(i-1).get(y2) == 0 && array.get(i-1).get(j) == 1))
                    array.get(i).set(j,1);
                else
                    array.get(i).set(j,0);
            }
        }
        return array;
    }
    private ObservableList<ObservableList<Integer>> calculateByRule60(ObservableList<ObservableList<Integer>> array) {
        for(int i = 1; i < array.size(); i++)
        {
            for(int j = 0; j < array.get(0).size(); j++)
            {
                periodicCondition(j,array.get(0).size());
                if((array.get(i-1).get(y1)== array.get(i-1).get(y2) && array.get(i-1).get(y1)!= array.get(i-1).get(j))
                    || (array.get(i-1).get(y2) == array.get(i-1).get(j) && array.get(i-1).get(y1) != array.get(i-1).get(y2)))
                    array.get(i).set(j,1);
                else
                    array.get(i).set(j,0);
            }
        }
        return array;
    }
    private ObservableList<ObservableList<Integer>> calculateByRule90(ObservableList<ObservableList<Integer>> array) {
        for(int i = 1; i < array.size(); i++)
        {
            for(int j = 0; j < array.get(0).size(); j++)
            {
                periodicCondition(j,array.get(0).size());
                array.get(i).set(j,array.get(i-1).get(y1) != array.get(i-1).get(y2) ? 1: 0);
            }
        }
        return array;
    }
    private ObservableList<ObservableList<Integer>> calculateByRule120(ObservableList<ObservableList<Integer>> array)    {
        for(int i = 1; i < array.size(); i++)
        {
            for(int j = 0; j < array.get(0).size(); j++)
            {
                periodicCondition(j,array.get(0).size());
                if((array.get(i-1).get(y1)== 1 && array.get(i-1).get(j) == 1 && array.get(i-1).get(y2) == 0)
                    || (array.get(i-1).get(y1) == 1 && array.get(i-1).get(j) == 0 && array.get(i-1).get(y2) == 1)
                    || (array.get(i-1).get(y1) != array.get(i-1).get(y2) && array.get(i-1).get(j) == array.get(i-1).get(y2)))
                    array.get(i).set(j,1);
                else
                    array.get(i).set(j,0);
            }
        }
        return array;
    }
    private ObservableList<ObservableList<Integer>> calculateByRule225(ObservableList<ObservableList<Integer>> array) {
        for(int i = 1; i < array.size(); i++)
        {
            for(int j = 0; j < array.get(0).size(); j++)
            {
                periodicCondition(j,array.get(0).size());
                if ((array.get(i-1).get(y2) == array.get(i-1).get(j) && array.get(i-1).get(y1) == array.get(i-1).get(y2))
                        || (array.get(i-1).get(y1) == 1 && array.get(i-1).get(j) == 1 && array.get(i-1).get(y2) == 0)
                        || (array.get(i-1).get(y1) == 1 &&array.get(i-1).get(j) == 0 && array.get(i-1).get(y2) == 1))
                    array.get(i).set(j,1);
                else
                    array.get(i).set(j,0);
            }
        }
        return array;
    }
    public ObservableList<ObservableList<Integer>> calculate(Rules rule, ObservableList<ObservableList<Integer>> array)    {
        ObservableList<ObservableList<Integer>> returnArray = null;
        switch (rule) {
            case RULE30:
                return  calculateByRule30(array);
            case RULE60:
                return calculateByRule60(array);
            case RULE90:
                return calculateByRule90(array);
            case RULE120:
                return calculateByRule120(array);
            case RULE225:
                return  calculateByRule225(array);
        }
        return returnArray;
    }
}
