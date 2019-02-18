close all
%%
figure
fid = fopen('20151118_165820.pcm');
h = fread(fid,'int16','ieee-be');
result_h = filter([1 -0.9375], 1, h);
result_h = result_h./max(abs(result_h));

subplot(2,1,1),plot(result_h);
N = 32;
% for i = 2:6
i = 16
h=linspace(1,1, (i)*N);
%  h=hamming((i-1)*N);
%形成一个矩形窗，长度为N
En1=conv(h,result_h.*result_h);
%求卷积得其短时能量函数En
En1_normalize = En1./max(abs(En1));
subplot(2,1,2),plot(En1_normalize);
% subplot(2,1,2),plot(En1);
legend('E1');
fclose(fid);

%%
% figure
% fid = fopen('20151116_091356.txt');
% h = fread(fid,'int16','ieee-be');
% result_h = h./max(abs(h));
% subplot(2,1,1),plot(result_h);
% N = 32;
% % for i = 2:6
% i = 128
% h=linspace(1,1, (i-1)*N);
% %  h=hamming((i-1)*N);
% %形成一个矩形窗，长度为N
% En2=conv(h,result_h.*result_h);
% %求卷积得其短时能量函数En
% En2_normalize = En2./max(abs(En2));
% subplot(2,1,2),plot(En2_normalize);
% legend('E2');
% fclose(fid);

%%
figure
fid = fopen('20151118_170236.pcm');
h = fread(fid,'int16','ieee-be');
subplot(2,1,1),plot(h);
result_h = filter([1 -0.9375], 1, h);
result_h = result_h./max(abs(result_h));
N = 32;
% for i = 2:6
i = 16
h=linspace(1,1, (i)*N);
%  h=hamming((i-1)*N);
%形成一个矩形窗，长度为N
En3=conv(h,result_h.*result_h);
%求卷积得其短时能量函数En
En3_normalize = En3./max(abs(En3));
subplot(2,1,2),plot(En3_normalize);
% subplot(2,1,2),plot(En3);
legend('E3');
fclose(fid);


%%
startPoint = 1;
endPoint = size(En1_normalize);
figure;
plot(En1_normalize)
legend('En1_normalize');
for i= 1:size(En1_normalize)
    if(En1_normalize(i,1) > 0.0125)
        startPoint = i;
        break;
    end
end
startPoint
for i= size(En1_normalize):-1:1
    if(En1_normalize(i,1) > 0.0125)
        endPoint = i;
        break;
    end
end
endPoint
En_test_standard = En1_normalize(startPoint:endPoint);
figure;
plot(En_test_standard)
legend('En_test_standard');
%%
startPoint = 1;
figure;
plot(En3)
legend('En3_normalize');
for i= 1:size(En3_normalize)
    if(En3_normalize(i,1) > 0.0125)
        startPoint = i;
        break;
    end
end
for i= size(En3_normalize):-1:1
    if(En3_normalize(i,1) > 0.0125)
        endPoint = i;
        break;
    end
end
endPoint
startPoint
En_test_compare = En3_normalize(startPoint:size(En_test_standard)+startPoint-1);
figure;
plot(En_test_compare)
legend('En_test_compare');
dot(En_test_compare, En_test_standard)/(norm(En_test_compare)*norm(En_test_standard))