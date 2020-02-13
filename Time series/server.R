library(shiny)

one <- read.csv("/home/sdixit/Desktop/App-1/Data/one.csv")
two <- read.csv("/home/sdixit/Desktop/App-1/Data/two.csv")
three <- read.csv("/home/sdixit/Desktop/App-1/Data/three.csv")
four <- read.csv("/home/sdixit/Desktop/App-1/Data/four.csv")
five <- read.csv("/home/sdixit/Desktop/App-1/Data/five.csv")
six <- read.csv("/home/sdixit/Desktop/App-1/Data/six.csv")
seven <- read.csv("/home/sdixit/Desktop/App-1/Data/seven.csv")

# Define server logic required to summarize and view the selected
# dataset
function(input, output) {
  
  # Return the requested dataset
  datasetInput <- reactive({
    switch(input$dataset,
           "2011" = one,
           "2012" = two,
           "2013" = three,
           "2014" = four,
           "2015" = five,
           "2016" = six,
           "2017" = seven)
  })
  output$distPlot <- renderPlot({
    #x    <- faithful[, 2]  # Old Faithful Geyser data
    #bins <- seq(min(x), max(x), length.out = input$bins + 1)
    dataset <- datasetInput()
    # draw the histogram with the specified number of bins
    plot(dataset$linear,dataset$Count, col="red", main = "Actual Vs. Predicted", sub = "Blue: Predicted, Red: Actual", xlab = "Months",ylab = "Number of Crimes")
    lines(dataset$linear, dataset$predict,col='blue')
    lines(dataset$linear, dataset$Count,col='red')
    
  })
  # Generate a summary of the dataset
  output$summary <- renderPrint({
    dataset <- datasetInput()
    summary(dataset)
  })
  
  # Show the first "n" observations
  output$view <- renderTable({
  head(datasetInput(), n = input$obs)
  })
}