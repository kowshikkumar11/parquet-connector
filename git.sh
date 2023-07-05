   #!/bin/bash
   
   echo -e "\nenter the remote origin url"
   read remote_url
   
   git init
   git remote add origin ${remote_url}
   git checkout -b dev
   git add .
   git commit -m "initial commit"
   git push --set-upstream origin dev 

