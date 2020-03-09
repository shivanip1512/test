class NameForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {value: '', id: ''};

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    this.setState({value: event.target.value});
  }

  handleSubmit(event) {
      alert('A name was submitted: ' + this.state.value);
      event.preventDefault();
      let state = this.state;
      let self = this;
      let paoId = '';
    
    // Simple POST request with a JSON body using fetch
    const generateTokenRequestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username : 'yukon', password : 'Yukon' })
    };
    
    fetch('http://localhost:8080/yukon/api/token', generateTokenRequestOptions).then(function (response) {
        response.json().then(function (data) {
            console.log(data.token);
            const createProgramConstraintRequestOptions = {
                    method: 'POST',
                    headers: new Headers({ 
                        'Content-Type' : 'application/json',
                        'Authorization' : 'Bearer ' + data.token,
                        'Host' : 'localhost:8080'}),
                        body : JSON.stringify({ name : state.value })
                };
            fetch("http://localhost:8080/yukon/api/dr/setup/controlScenario/create", createProgramConstraintRequestOptions)
            .then(response1 => response1.json())
            .then(function (data1) {
                console.log(data1.paoId);
                self.setState({id: data1.paoId})
            });
        });
    });
  }

  render() {
    return (
       <div className="dib">
           <form onSubmit={this.handleSubmit}>
               <label>
                   Control Scenario Name:&nbsp;<input type="text" value={this.state.value} onChange={this.handleChange} />
               </label>
               <input type="submit" value="Submit" className="fr"/>
            </form>
               Pao Id: {this.state.id}
        </div>
    );
  }
}

ReactDOM.render(
  <NameForm />,
  document.getElementById('root')
);