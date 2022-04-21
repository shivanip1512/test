import ReactDOM from 'react-dom';
import Input from '../../../components/controls/Input'
import { useDispatch } from 'react-redux';

const mockDispatch = jest.fn();
jest.mock("react-redux", () => ({
  useSelector: jest.fn(),
  useDispatch: () => mockDispatch,
}));

it("render input without crashing", () => {
  const div = document.createElement("div");
  ReactDOM.render(
    <Input
      label="Port Number"
      name="portNumber"
      value="testValue"
      maxLength={5}
      style={{ float: "right" }}
    />,
    div
  );
});