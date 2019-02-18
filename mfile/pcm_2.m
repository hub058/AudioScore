%%
figure
fid = fopen('20151116_142456.txt');
h = fread(fid,'int16','ieee-be');
result_h = h./max(abs(h));
% result_h = filter([1 -0.9375], 1, result_h);
% result_h = result_h./max(abs(result_h));
subplot(2,1,1),plot(result_h);
N = 32;
% for i = 2:6
i = 128
h=linspace(1,1, (i-1)*N);
%  h=hamming((i-1)*N);
%形成一个矩形窗，长度为N
En_test=conv(h,result_h.*result_h);
%求卷积得其短时能量函数En
% En_test_normalize = En_test./max(abs(En_test));
% subplot(2,1,2),plot(En_test_normalize);
subplot(2,1,2),plot(En_test);
legend('E3');
fclose(fid);

%%

close all
En_standard = En1_normalize(97751:169000);
figure
plot(En_standard)
En_compare = En3_normalize(43941:43940+71250);
figure
plot(En_compare)
dot(En_compare, En_standard)/(norm(En_compare)*norm(En_standard))

%%

En_test = En_test_normalize(1:71250);
figure
plot(En_test)
dot(En_test, En_standard)/(norm(En_test)*norm(En_standard))


%%
for i=1:n-1
  if a(i)>=0
    b(i)= 1;
  else
    b(i) = -1;
  end
  if a(i+1)>=0
    b(i+1)=1;
  else
    b(i+1)=-1;
  end
  w(i)=abs(b(i+1)-b(i));
end 