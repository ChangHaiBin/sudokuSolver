import java.io.*;
import java.util.Scanner;
import java.io.IOException;

//Remark: We only solve for one particular solution.
//There may be more than one solution to a problem with insufficient info.
public class SudokuSolver{
    private int[][][] board=new int[10][10][10];
    private int[][] SOLUTION=new int[10][10];
    private boolean changed=false;
    private boolean unsolvable=false;
    private int numberofguesses=0;
    
    //Assume 1<= {i,j,k} <=9, then
    //board[i][j][k] = 0 when we are sure that the i-th row, j-th column 
    //cannot be filled with k.
    //board[i][j][k] = 2 when we already filled the i-th row, j-th column 
    //with the number k
    //board[i][j][k] = 1 when we are not sure.
    
    //the int[][] SOLUTION is just for convenience.
    
    //the "changed" variable check if we inserted more numbers based only on 
    //elementary logic deduction.
    //If elementary logic deduction fails, then we resort to guessing.
    
    
    //the "unsolvable" variable is true when we encounter an error,
    //e.g. two numbers in the same row, or no numbers can go into a specific spot, etc.
    
    
    
    //Initialize empty Sudoku puzzle
    public SudokuSolver(){
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                for(int k=1;k<=9;k++){
                    board[i][j][k]=1;
                }
            }
        }
    }
    
    //Initialize partially completed puzzle
    public SudokuSolver(int[][] partial){
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                for(int k=1;k<=9;k++){
                    board[i][j][k]=1;
                }
            }
        }
        
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                if(partial[i][j]<0 || partial[i][j]>9){
                    throw new IllegalArgumentException();
                }
                if(partial[i][j]!=0){
                    insert(i,j,partial[i][j]);
                }
            }
        }
    }
    
    
    private void SOLVEIT(){
        do{
            changed=false;
            checkrow();
            checkcol();
            checkblock();
            
            //Check the 9 smaller sub-grid of size 3x3.
            checksquare(1,3,1,3);
            checksquare(1,3,4,6);
            checksquare(1,3,7,9);
            checksquare(4,6,1,3);
            checksquare(4,6,4,6);
            checksquare(4,6,7,9);
            checksquare(7,9,1,3);
            checksquare(7,9,4,6);
            checksquare(7,9,7,9);
            if(unsolvable){
                return;
            }
        } while(changed!=false);
        
        boolean SOLVED=this.solved();

        if(!SOLVED){
            int ROW=0, COL=0;
            
            //Find any spot not yet filled.
            for(int i=1;i<=9;i++){
                for(int j=1;j<=9;j++){
                    if(SOLUTION[i][j]==0){
                        ROW=i;
                        COL=j;
                    }
                }
            }
            
            
            for(int k=1;k<=9;k++){
                //Guess using numbers not yet eliminated.
                if(board[ROW][COL][k]==1){
                    SudokuSolver GUESS1=new SudokuSolver(SOLUTION);
                    GUESS1.insert(ROW,COL,k);
                    GUESS1.numberofguesses=this.numberofguesses+1;
                    GUESS1.SOLVEIT();
                    
                    
                    //Get the answer from the guess if solved.
                    if(GUESS1.unsolvable!=true && GUESS1.solved()==true){
                        this.SOLUTION=GUESS1.SOLUTION;
                        this.numberofguesses=GUESS1.numberofguesses;
                        return;
                    }
                }
            }
            
        }
    }
    
    //Check if it is solved.
    private boolean solved(){
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                if(SOLUTION[i][j]==0){
                    return false;
                }
            }
        }
        return true;
    }
    
    //Check if we can insert the number k into the j-th column.
    private void checkcol(){
        for(int j=1;j<=9;j++){
            for(int k=1;k<=9;k++){
                int counter=0;
                int ROW=0;
                for(int i=1;i<=9;i++){
                    if(board[i][j][k]==2){
                        counter=9;
                        break;
                    } else if(board[i][j][k]==1){
                        counter++;
                        ROW=i;
                    }
                }
                if(counter==1){
                    insert(ROW,j,k);
                } else if(counter==0){
                    unsolvable=true;
                    return;
                }
            }
        }
    }
    
    //Check if we can insert the number k into the i-th row.
    private void checkrow(){
        for(int i=1;i<=9;i++){
            for(int k=1;k<=9;k++){
                int counter=0;
                int COL=0;
                for(int j=1;j<=9;j++){
                    if(board[i][j][k]==2){
                        counter=9;
                        break;
                    } else if(board[i][j][k]==1){
                        counter++;
                        COL=j;
                    }
                }
                if(counter==1){
                    insert(i,COL,k);
                }else if(counter==0){
                    unsolvable=true;
                    return;
                }
            }
        }
    }
    
    //Check if any block already has 8 choices eliminated.
    private void checkblock(){
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                int counter=0;
                int RESULT=0;
                for(int k=1;k<=9;k++){
                    if(board[i][j][k]==2){
                        counter=9;
                        break;
                    } else if(board[i][j][k]==1){
                        RESULT=k;
                        counter++;
                    }
                }
                if(counter==1){
                    insert(i,j,RESULT);
                }else if(counter==0){
                    unsolvable=true;
                    return;
                }
            }
        }
    }
    
    //Check if we can insert the number k into a subgrid of size 3x3
    private void checksquare(int rmin, int rmax, int cmin, int cmax){
        for(int k=1;k<=9;k++){
            int counter=0;
            int ROW=0, COL=0;
            for(int i=rmin;i<=rmax;i++){
                for(int j=cmin;j<=cmax;j++){
                    if(board[i][j][k]==2){
                        counter=9;
                        break;
                    } else if(board[i][j][k]==1){
                        counter++;
                        ROW=i;
                        COL=j;
                    }
                }
            }
            
            if(counter==1){
                insert(ROW,COL,k);
            }else if(counter==0){
                    unsolvable=true;
                    return;
            }
        }
    }
    
    private void insert(int row, int col, int solution){
        
        if(row<1 || row>9 || col<1 || col>9 || solution<1 || solution>9){
            throw new IllegalArgumentException();
        }
        
        //If the block is already filled with another number
        if(SOLUTION[row][col]!=0 && SOLUTION[row][col]!=solution){
            unsolvable=true;
            return;
        }
        SOLUTION[row][col]=solution;
        
        //Make sure that
        //Other blocks in the same row or column cannot have the "solution" number
        //The specific spot where we put our number "solution" will not have other solutions.
        
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                for(int k=1;k<=9;k++){
                    if(i==row){
                        if(j==col){
                            if(k==solution){
                                if(board[i][j][k]==0){
                                    unsolvable=true;
                                    return;
                                }
                                board[i][j][k]=2;
                            } else{
                                if(board[i][j][k]==2){
                                    unsolvable=true;
                                    return;
                                }
                                board[i][j][k]=0;
                            }
                        } else{
                            if(k==solution){
                                if(board[i][j][k]==2){
                                    unsolvable=true;
                                    return;
                                }
                                board[i][j][k]=0;
                            }
                        }
                    } else{
                        if(j==col && k==solution){
                            if(board[i][j][k]==2){
                                    unsolvable=true;
                                    return;
                                }
                            board[i][j][k]=0;
                        }
                    }
                }
            }
        }
        
        //Eliminate the "solution" number from blocks of the same 3x3 subgrid.
        int rmin, rmax,cmin,cmax;
        if(row<=3){
            rmin=1;
            rmax=3;
        } else if(row<=6){
            rmin=4;
            rmax=6;
        } else{
            rmin=7;
            rmax=9;
        }
        if(col<=3){
            cmin=1;
            cmax=3;
        } else if(col<=6){
            cmin=4;
            cmax=6;
        } else{
            cmin=7;
            cmax=9;
        }
        for(int i=rmin;i<=rmax;i++){
            for(int j=cmin;j<=cmax;j++){
                if(i!=row || j!=col){
                    if(board[i][j][solution]==2){
                        unsolvable=true;
                        return;
                    } else{
                        board[i][j][solution]=0;
                    }
                }
            }
        }
        changed=true;
    }
    
    
    public void writeToString(){
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                if(j==3 || j==6){
                    System.out.print(SOLUTION[i][j]+"|");
                } else if(j==9){
                    System.out.print(SOLUTION[i][j]);
                } else {
                    System.out.print(SOLUTION[i][j]+" ");
                }
            }
            System.out.println();
            if(i==3||i==6){
                System.out.println("-----------------");
            }
        }
        System.out.println("Number of Guesses ="+this.numberofguesses);
    }
    
    public static void main(String args[]) throws IOException{
        File f = new File("sudoku.txt");
        
        Scanner sc=new Scanner(f);
        int[][] SAMPLE=new int[10][10];
        int row=1;
        int discard=1;
        int counter=1;
        int NUMBERGUESS=0;
        while(sc.hasNext()){
            String next=sc.next();
            if(discard==1 || discard==2){
                
            } else{
                for(int j=1;j<=9;j++){
                    SAMPLE[row][j]=Integer.parseInt(next.substring(j-1,j));
                }
                row++;
                
            }
            if(discard==11){
                discard=1;
                row=1;
                SudokuSolver ORIGINALQUESTION=new SudokuSolver(SAMPLE);
                ORIGINALQUESTION.SOLVEIT();
                System.out.println("The solution to problem number "+counter+" is:");
                counter++;
                //NUMBERGUESS+=ORIGINALQUESTION.numberofguesses;
                ORIGINALQUESTION.writeToString();
            } else{
                discard++;
            }
        }
        
                //System.out.println(NUMBERGUESS);
        /*
        int[][] SAMPLE={{5,0,4,0,0,3,0,0,0},
                        {0,6,0,0,2,0,0,1,0},
                        {8,0,0,0,0,1,0,0,6},
                        {0,7,0,0,0,0,8,0,0},
                        {4,0,0,0,5,0,0,0,7},
                        {0,0,5,0,0,0,0,9,0},
                        {7,0,0,9,0,0,0,0,1},
                        {0,9,0,0,1,0,0,4,0},
                        {0,0,0,6,0,0,7,0,9}
        };
        int[][] SAMPLE2=new int[10][10];
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                SAMPLE2[i][j]=SAMPLE[i-1][j-1];
            }
        }
        SudokuSolver HELLO2=new SudokuSolver(SAMPLE2);
        HELLO2.SOLVEIT();
        HELLO2.writeToString();
        SudokuSolver HELLO=new SudokuSolver();
        
        HELLO.insert(2,1,9);
        HELLO.insert(3,1,8);
        HELLO.insert(4,2,4);
        HELLO.insert(5,5,4);
        HELLO.insert(6,8,4);
        HELLO.insert(7,3,4);
        HELLO.insert(8,4,4);
        HELLO.insert(9,7,4);
        HELLO.SOLVEIT();
        HELLO.writeToString();
        */
    }
}