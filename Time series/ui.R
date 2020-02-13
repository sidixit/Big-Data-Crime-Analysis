library(shiny)

# Define UI for dataset viewer application
fluidPage(
  
  # Application title
  titlePanel("Crime Rate Predictions"),
  
  # Sidebar with controls to select a dataset and specify the
  # number of observations to view
  sidebarLayout(
    sidebarPanel(
      selectInput("dataset", "Choose a dataset:", 
                  choices = c("2011", "2012", "2013","2014","2015","2016","2017")),
      
      numericInput("obs", "Number of observations to view:", 12)
    ),
    
    # Show a summary of the dataset and an HTML table with the 
    # requested number of observations
    mainPanel(      
      plotOutput("distPlot"),

      verbatimTextOutput("summary"),

      tableOutput("view")

    )
  )
)