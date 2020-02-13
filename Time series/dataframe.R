#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


#Initialize and set system environment for R Studio to refer to
if (nchar(Sys.getenv("SPARK_HOME")) < 1) {
  Sys.setenv(SPARK_HOME = "/home/sdixit/spark-1.6.1-bin-hadoop1")
}

#Read SparkR library inside Spark folder
library(SparkR, lib.loc = c(file.path(Sys.getenv("SPARK_HOME"), "R", "lib")))
sc <- sparkR.init(master = "local[*]", sparkEnvir = list(spark.driver.memory="2g"))

#sc <- sparkR.init()
sqlContext <- sparkRSQL.init(sc)

newdata <- read.csv("/home/sdixit/Downloads/project.csv")
df <- createDataFrame(sqlContext, newdata)

monthnames <- df$Month

df$Month <- NULL

head(df)

model <- glm(Count~.,data = df)

summary(model)

predicted <- predict(model, df)

# Print the teenagers in our dataset 
print(predicted)

output <- collect(predicted)

newdata$predict <- output$prediction

output$prediction
write.csv(newdata,"/home/sdixit/predicted1.csv")

plot(newdata$linear,newdata$Count, col="red", main = "Actual Vs. Predicted", sub = "Blue: Predicted, Red: Actual", xlab = "Months",ylab = "Number of Crimes")
lines(newdata$linear, newdata$predict,col='blue')
lines(newdata$linear, newdata$Count,col='red')

mean(newdata$Count)

mean(newdata$predict)

a <- mean(abs((newdata$Count-newdata$predict)/newdata$Count)*100)

print(a)

#Split the dataset into 7 parts

one <- newdata[1:12,]
two <- newdata[13:24,]
three <- newdata[25:36,]
four <- newdata[37:48,]
five <- newdata[49:60,]
six <- newdata[61:72,]
seven <- newdata[73:76,]

write.csv(one,"/home/sdixit/Desktop/App-1/Data/one.csv") 
write.csv(two,"/home/sdixit/Desktop/App-1/Data/two.csv") 
write.csv(three,"/home/sdixit/Desktop/App-1/Data/three.csv") 
write.csv(four,"/home/sdixit/Desktop/App-1/Data/four.csv") 
write.csv(five,"/home/sdixit/Desktop/App-1/Data/five.csv") 
write.csv(six,"/home/sdixit/Desktop/App-1/Data/six.csv") 
write.csv(seven,"/home/sdixit/Desktop/App-1/Data/seven.csv") 

# Stop the SparkContext now
sparkR.stop()

#Run the Shiny front end:

install.packages("shiny")
library(shiny)
runApp("/home/sdixit/Desktop/App-1")
