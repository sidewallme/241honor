imageTrain = loadMNISTImages('train-images.idx3-ubyte');  
 labelTrain = loadMNISTLabels('train-labels.idx1-ubyte');  
 imageTest = loadMNISTImages('t10k-images.idx3-ubyte');  
 labelTest = loadMNISTLabels('t10k-labels.idx1-ubyte');  
 trainSize = size(imageTrain);  
 testSize = size(imageTest);  
 assert(trainSize(2) == length(labelTrain), 'Train image and train label size mismatches!');  
 assert(testSize(2) == length(labelTest), 'Test image and test label size mismatches!');  
 assert(trainSize(1) == testSize(1), 'Train and test image size mismatches!');  
 N = trainSize(2);  
 TOTAL_HU = 300;  
 TOTAL_IN = trainSize(1);  
 TOTAL_OUT = 10;  
 MAX_ITERATION = 100;  
 BETA = 0.01; % Scaling factor in sigmoid function  
 ALPHA = 0.1; % Learning rate  
 %% Initialize the weights from Uniform(0, 1)  
 w1 = rand(TOTAL_IN + 1, TOTAL_HU); % Weight for layer-1, including bias unit  
 w2 = rand(TOTAL_HU + 1, TOTAL_OUT); % Weight for layer-2. including bias unit  
 %% Temporary matrix to store weight updates and activations  
 x1 = zeros(TOTAL_IN + 1, 1);  
 x2 = zeros(TOTAL_HU + 1, 1);  
 x22 = zeros(TOTAL_HU, 1);  
 x3 = zeros(TOTAL_OUT, 1);  
 e1 = zeros(TOTAL_IN + 1, 1);  
 e2 = zeros(TOTAL_HU + 1, 1);  
 e3 = zeros(TOTAL_OUT, 1);  
 dw1 = zeros(TOTAL_IN + 1, TOTAL_HU);  
 dw2 = zeros(TOTAL_HU + 1, TOTAL_OUT);  
 A = eye(TOTAL_OUT);  
 success = zeros(MAX_ITERATION, 1);  
 %% Training  
 for t = 1:MAX_ITERATION  
   fprintf('Iteration %d\n', t);  
   perm = randperm(N);  
   for p = 1:N  
     if (mod(p, 1000) == 0)  
       fprintf('\t%d\n', p);  
     end  
     % Propogate forward  
     index = perm(p);  
     x1 = [imageTrain(:, index);1];  
     x22 = sigmf(w1' * x1, [BETA, 0]);  
     x2 = [x22; 1];  
     x3 = sigmf(w2' * x2, [BETA, 0]);  
     % Propogate backward  
     e3 = x3 - A(:, labelTrain(index) + 1);  
     e2 = w2 * (e3 .* x3 .* (1 - x3));  
     e2 = e2(1:TOTAL_HU);  
     e1 = w1 * e2;  
     % Update weight  
     w2 = w2 - ALPHA * x2 * (e3 .* x3 .* (1 - x3))';  
     w1 = w1 - ALPHA * x1 * (e2 .* x22 .* (1 - x22))';  
   end  
   %% Check training error  
   success(t) = 0;  
   for i = 1:N  
     x1 = [imageTrain(:, i); 1];  
     x2 = [sigmf(w1' * x1, [BETA, 0]); 1];  
     x3 = sigmf(w2' * x2, [BETA, 0]);  
     [dummy, m] = max(x3);  
     if (m == labelTrain(i) + 1)  
       success(t) = success(t) + 1;  
     end  
   end  
 end  
 testSuccess = 0;  
 for i = 1:testSize(2)  
   x1 = [imageTest(:, i); 1];  
   x2 = [sigmf(w1' * x1, [BETA, 0]); 1];  
   x3 = sigmf(w2' * x2, [BETA, 0]);  
   [dummy, m] = max(x3);  
   if (m == labelTest(i) + 1)  
     testSuccess = testSuccess + 1;  
   end  
 end  