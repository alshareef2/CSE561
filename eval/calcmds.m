function [diff_matrix] = calcmds(filename)

    %% Read all of the datasets as csv
    egypt = csvread('../stats/egypt_htstats.csv', 1, 2);
    ows = csvread('../stats/ows_htstats.csv', 1, 2);
    turkey = csvread('../stats/turkey_htstats.csv', 1, 2);
    
    egypt = egypt(:,1);
    ows = ows(:, 1);
    turkey = turkey(:, 1);

    %% Read our stats file
    ourFile = dlmread(filename, '\t', 1, 1);
    ourFile = ourFile(:,1);
    
    %% Compare all of the dtw distances
    series = {egypt, ows, turkey, ourFile};
    labels = {'egypt', 'ows', 'turkey', 'model'};
    num_datasets = size(labels, 2);
    arraylen = min([size(egypt, 1), size(ows, 1), size(turkey, 1), size(ourFile, 1)]);
    display(arraylen);
    
    diff_matrix = zeros(num_datasets, num_datasets);
    
    for i = 1:num_datasets
        for j = (i + 1):num_datasets
            
%             [dist accumD k w] = dtw(series{i}(1:arraylen)', series{j}(1:arraylen)');
%             v = mean(mean(accumD));

            v = vanilla(series{i}(1:arraylen)', series{j}(1:arraylen)');

            diff_matrix(i, j) = v;
            diff_matrix(j, i) = v;
        end
    end
    
    %% Plot the MDS 
	[Y stress] = mdscale(diff_matrix, 2);
	plot(Y(:,1),Y(:,2),'bx');
	text(Y(:,1),Y(:,2),labels,'HorizontalAlignment','left');
    
end
