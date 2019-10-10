#!/bin/sh
java_dir=../../main/java
python_dir=./python
cs_dir=./csharp
python_dest_dir=../../../../../../vscode/qingzhenyun-python-toolbox
python_dest_rpc_dir=rpcs
node_dest_dir=../../../../../../vscode/qingzhenyun-api-gateway/src/app/ice
node_dir=./javascript
python_split=${python_dest_rpc_dir}.
rm -rf ${python_dir}
rm -rf ${node_dir}
rm -rf ${cs_dir}
mkdir -p ${python_dir}
mkdir -p ${python_dir}/${python_dest_rpc_dir}
mkdir -p ${node_dir}
mkdir -p ${cs_dir}
for file in ./*.ice
do
    if test -f ${file}
    then
        echo ${file}
        slice2java ${file} -I ./
        slice2py ${file} --output-dir=${python_dir} -I ./ --prefix=${python_split}
        # convert java
        slice2js ${file} --output-dir=${node_dir} -I ./
        slice2cs ${file} --output-dir=${cs_dir} -I ./
    fi

#    cp -r ${python_dir}/ ${python_dest_dir}
#    cp -r ${node_dir}/ ${node_dest_dir}
done
cp -r ./com ${java_dir}
# do js file
echo "Checking javascript files..."
for file in ${node_dir}/*.js
do
    if test -f ${file}
    then
        echo ${file}
        sed -i "" "s#require(\"common\")#require(\".\/common\")#g" ${file}
    fi
done
cp -r ${node_dir}/ ${node_dest_dir}
rsp=${python_dest_rpc_dir}/
for file in ${python_dir}/${python_split}*
do
    if test -f ${file}
    then
        echo processing ${file}
        mv ${file} ${file/${python_split}/${rsp}}
    fi
done
cp -r ${python_dir}/${rsp}common_ice.py ${python_dir}
cp -r ${python_dir}/ ${python_dest_dir}