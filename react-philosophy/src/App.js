import './App.css';
import React from 'react'

//初始数据
let data = [
  {category: "Sporting Goods", price: "$49.99", stocked: true, name: "Football"},
  {category: "Sporting Goods", price: "$9.99", stocked: true, name: "Baseball"},
  {category: "Sporting Goods", price: "$29.99", stocked: false, name: "Basketball"},
  {category: "Electronics", price: "$99.99", stocked: true, name: "iPod Touch"},
  {category: "Electronics", price: "$399.99", stocked: false, name: "iPhone 5"},
  {category: "Electronics", price: "$199.99", stocked: true, name: "Nexus 7"}
]

class FilterableProductTable extends React.Component {
  constructor(props) {
      super(props)
      this.onSearchTermChange = this.onSearchTermChange.bind(this)
      this.onOnlyStockChange = this.onOnlyStockChange.bind(this)
      this.state = {
          searchTerm: '', //搜索词
          onlyStock: false, //是否只显示股票
      }
  }

  onSearchTermChange(e) {
      this.setState({
          searchTerm: e.target.value
      })
  }

  onOnlyStockChange(e) {
      this.setState({
          onlyStock: e.target.value
      })
  }

  render() {
      return (
          <div>
              <SearchBar searchTerm={this.state.searchTerm} onlyStock={this.state.onlyStock}
               onSearchTermChange={this.onSearchTermChange} onOnlyStockChange={this.onOnlyStockChange}/>
              <div>
                  <div>
                      <span className="item">Name</span>
                      <span className="item">Price</span>
                  </div>
                  <div>
                    <ProductTable data={this.props.data} 
                      searchTerm={this.state.searchTerm} onlyStock={this.state.onlyStock}
                      onSearchTermChange={this.onSearchTermChange} ononlyStockChange={this.onOnlyStockChange}/>
                  </div>
              </div>
          </div>
      )
  }

}

class SearchBar extends React.Component {
  constructor(props) {
      super(props)
  }

  render() {
      return (
          <div>
              <div>
                  <input type='text' placeholder='Search...' value={this.props.searchTerm} onChange={this.props.onSearchTermChange}></input>
              </div>
              <div>
                  <input type='radio' value={this.props.onlyStock} onChange={this.props.onOnlyStockChange}></input>
                  Only show products in stock
              </div>
          </div>
      )
  }

}

class ProductTable extends React.Component {
  constructor(props) {
      super(props)
  }

  render() {
      let categoryMap = new Map()
      this.props.data.forEach(item => {
          let list = categoryMap.get(item.category)
          if (!list) {
              list = []
              categoryMap.set(item.category, list)
          }
          list.push(item)
      })
      return (
          <div>
              {
                  Array.from(categoryMap.keys()).map(category => {
                      let list = categoryMap.get(category).filter(item => item.name.includes(this.props.searchTerm))
                      if (this.props.onlyStock) {
                        list = list.filter(item => item.stocked)
                      }
                      return (
                          <React.Fragment key={category}>
                              <ProductCategoryRow category={category}/>
                              {
                                  list.map(item => {
                                      return <ProductRow key={category + '' + item.name} product={item}/>
                                  })
                              }
                          </React.Fragment>
                      )
                  })
              }
          </div>
      )
  }
}

class ProductCategoryRow extends React.Component {
  constructor(props) {
      super(props)
  }

  render() {
      return (
          <div>
              <span>{this.props.category}</span>
          </div>
      )
  }
}

class ProductRow extends React.Component {
  constructor(props) {
      super(props)
  }
  render() {
      return (
          <div>
              <span className="item" style={{color: this.props.product.stocked ? 'black' : 'red'}}>{this.props.product.name}</span>
              <span className="item">{this.props.product.price}</span>
          </div>
      )
  }
}

function App() {
  return (
      <FilterableProductTable className="container" data={data} />
  );
}

export default App;
