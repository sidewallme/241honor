#include <vector>
#include <iomanip>
#include <string>
#include <map>
#include <random>
#include <cmath>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <fstream>



using namespace std;


//========USER DEFINED VAR AREA========//
const int input_number = 2/*CHANGE HERE*/;
const int hiddenUnits_number = 2/*CHANGE HERE*/;
const int output_number = 2/*CHANGE HERE*/;
int set_num = 100/*CHANGE HERE*/;
double rate = 0.5/*CHANGE HERE*/;
int iter_limit = 40/*CHANGE HERE*/;
//===============AREA END==============//

struct hiddenUnit{
	double flow_in;
	double flow_out;
};

/*structure of output Unit*/
struct outputUnit{
	double flow_in;
	double output;
};

/*read file function*/
bool read(std::vector<std::vector<double>> &data, const char* file_path);

/*the three datasets*/
std::vector<std::vector<double> >  dataset;
std::vector<std::vector<double> >  inputs;
std::vector<std::vector<double> >  targets;

/*declare the hiddenLayer and outputLayer*/
std::vector<hiddenUnit>  hiddenLayer(hiddenUnits_number);
std::vector<outputUnit>  outputLayer(output_number);

/*weights from input to hiddenLayer and hiddenLayer to outputLayer*/
double inputToHidden_weights[input_number][hiddenUnits_number] = {};
double hiddenToOut_weights[hiddenUnits_number][output_number] = {};

/*the change of each weight*/
double dW[input_number][hiddenUnits_number] = {};
double dH[hiddenUnits_number][output_number] = {};

/*error of each unit*/
double sigma_out[output_number];
double sigma_w[hiddenUnits_number];

/*structure of hidden Unit*/

/*output function :: sigmoid function*/
double outputfunction(double value);

/*initialize all the changes of weight (△weights) to zero*/
void iniToZeros();

/*run over the whole network to get value of each unit*/
void runOver(std::vector<double> inputing);

/*Back Propagation to calculate errors and updates*/
void BP(std::vector<double> inputing, std::vector<double> targeting);

/*Update each weight*/
void updateWeights();

/*extract input data and target data from the whole dataset*/
void extractInputAndTargert();

std::vector<std::vector<double>> t_inputs;


/**
* To imporve the performance of BP, I adopted the SCAWI initialization method;
* For more information, please refer to http://bit.ly/1qCQYII
*Statistically controlled activation weight initialization(SCAWI).
*By: Gian Paolo Drago, Sandro Ridella
*In : IEEE Transactions on Neural Networks, 1992
*/

//=============main function=============//
void iniWeightsBySCAWI();
//=============sub functions=============//
//	transpose the 2d vector
void transpose();

//	calculate the average value of a vector;
double calculate_avg(std::vector<double> in);

//	calculate the variance value of a vector;
double calculate_var(std::vector<double> in, double avg);

//	generate a uniformly distributed real number between -1 and 1;
double uniform_dist_rand_generator();
//=================ENDING================//




int main(int argc, const char * argv[])
{
	if (argc != 2){
		cout << "Usage: [FILEPATH]" << endl;
		return 1;
	}

	if (!read(dataset, argv[1])){
		cout << "Can't open the file. Please enter a valid file path." << endl;//
		system("PAUSE");
		return 1;
	}
	extractInputAndTargert();
	//Using SCAWI to initialize the weights[i,j][j,k];
	iniWeightsBySCAWI();
	for (int j = 0; j < iter_limit; j++){
		//set delta W to zeros;
		iniToZeros();
		for (int i = 0; i < set_num; i++){
			//input the example
			vector<double> targeting = targets[i];
			vector<double> inputing = inputs[i];
			//compute the network outputs;
			runOver(inputing);
			// propagate errors backward
			BP(inputing, targeting);
		}
		//update weights;
		updateWeights();
	}
	cout << "FINISHED" << endl;
	cout << "press something to continue...";
	getchar();//for viewing, pause;
	return 0;
}


double outputfunction(double value){
	double result = 1 / (1 + exp(-value));
	return result;
}

void iniToZeros(){
	for (int i = 0; i < input_number; i++){
		for (int j = 0; j < hiddenUnits_number; j++){
			dW[i][j] = 0;
		}
	}
	for (int i = 0; i < hiddenUnits_number; i++){
		for (int j = 0; j < output_number; j++){
			dH[i][j] = 0;
		}
	}
}

void runOver(vector<double> inputing){
	for (int j = 0; j < hiddenUnits_number; j++){
		double flow_in=0.0;
		for (int i = 0; i < input_number; i++){
			flow_in += (inputing[i] * inputToHidden_weights[i][j]);
		}
		hiddenLayer[j].flow_in = flow_in;
		hiddenLayer[j].flow_out = outputfunction(flow_in);
	}

	for (int k = 0; k < output_number; k++){
		double flow_in=0.0;
		for (int j = 0; j < hiddenUnits_number; j++){
			flow_in += (hiddenLayer[j].flow_out * hiddenToOut_weights[j][k]);
		}
		outputLayer[k].flow_in = flow_in;
		outputLayer[k].output = outputfunction(flow_in);
	}
}

void BP(vector<double> inputing, vector<double> targeting){
	for (int k = 0; k < output_number; k++){
		sigma_out[k] = outputLayer[k].output * (1 - outputLayer[k].output)*(targeting[k] - outputLayer[k].output);
	}
	for (int j = 0; j < hiddenUnits_number; j++){
		double temp=0.0;
		for (int k = 0; k < output_number; k++){
			temp += hiddenToOut_weights[j][k] * sigma_out[k];
		}
		sigma_w[j] = hiddenLayer[j].flow_out * (1 - hiddenLayer[j].flow_out)*temp;
	}
	for (int j = 0; j < hiddenUnits_number; j++){
		for (int k = 0; k < output_number; k++){
			dH[j][k] += rate*sigma_out[k] * hiddenLayer[j].flow_out;
		}
	}

	for (int i = 0; i < input_number; i++){
		for (int j = 0; j < hiddenUnits_number; j++){
			dW[i][j] += rate*sigma_w[j] * inputing[i];
		}
	}
}

void updateWeights(){
	for (int i = 0; i < input_number; i++){
		for (int j = 0; j < hiddenUnits_number; j++){
			inputToHidden_weights[i][j]+=dW[i][j];
		}
	}
	for (int j = 0; j < hiddenUnits_number; j++){
		for (int k = 0; k < output_number; k++){
			hiddenToOut_weights[j][k] += dH[j][k];
		}
	}
}

void extractInputAndTargert(){
	for (int i = 0; i < set_num; i++){
		vector<double> row;
		row.push_back(1.0);
		for (int j = 0; j < input_number-1; j++){
			row.push_back(dataset[i][j]);
		}
		inputs.push_back(row);
	}
	
	for (int i = 0; i < set_num; i++){
		vector<double> row;
		for (int j = input_number-1; j < output_number+input_number-1; j++){
			row.push_back(dataset[i][j]);
		}
		targets.push_back(row);
	}
}


/*
 * SCAWI Functions
 */

void transpose(){
	for (int i = 0; i < input_number; i++){
		std::vector<double> temp;
		for (int c = 0; c < set_num; c++){
			temp.push_back(inputs[c][i]);
		}
		t_inputs.push_back(temp);
	}
}

void iniWeightsBySCAWI(){
	transpose();
	//firstly, do weights from input to hidden layer;
	for (int i = 0; i < input_number; i++){
		double avgr = calculate_avg(t_inputs[i]);
		double vari = calculate_var(t_inputs[i], avgr);
		for (int j = 0; j < hiddenUnits_number; j++){
			inputToHidden_weights[i][j] = (1.3 / sqrt(1 + input_number*(avgr*avgr + vari)))*uniform_dist_rand_generator();//SCAWI
		}
	}
	//secondly, do weights from hidden layer to output;
	for (int j = 0; j < hiddenUnits_number; j++){
		for (int k = 0; k < output_number; k++){
			hiddenToOut_weights[j][k] = (1.3 / sqrt(1 + hiddenUnits_number*0.3))*uniform_dist_rand_generator();//SCAWI
		}
	}
}

double calculate_avg(std::vector<double> in){
	double a = 0.0;
	for (int i = 0; i < set_num; i++){
		a += in[i];
	}
	return a / set_num;
}

//calculate the variance value of a vector;
double calculate_var(std::vector<double> in, double avg){

	double sum_s = 0.0;
	for (int i = 0; i < set_num; i++){
		sum_s += (in[i] - avg)*(in[i] - avg);
	}
	return sum_s / set_num;
}

double uniform_dist_rand_generator(){
	std::random_device rd;
	std::uniform_real_distribution<> d(-1, 1);
	return d(rd);
}

bool read(std::vector<std::vector<double>> &data, const char* file_path)
{
	std::string str(file_path);
	std::ifstream file(str);
	// if file cannot be read, return false;
	if (!file.is_open())
		return false;
	//read one line per time
	std::string line;
	
	while (std::getline(file, line))
	{
		std::vector<double>   lineData; //vector of double variables 
		std::stringstream  linestream(line); 
		double value; // single double variable 
		while (linestream >> value)
		{
			lineData.push_back(value);
		}
		data.push_back(lineData);//push vector<double> into dataset
	}
	return true;
}