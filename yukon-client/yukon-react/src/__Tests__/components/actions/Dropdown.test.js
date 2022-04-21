import ReactDOM from "react-dom";
import Dropdown from "../../../components/controls/Dropdown";

jest.mock("react-redux", () => ({
  useSelector: jest.fn((fn) => fn()),
}));

const dorpdownItems = [
  {
    label: "testDropdown",
    value: "testDropdownValue",
  },
];

it("render Dropdown without crashing", () => {
  const div = document.createElement("div");
  ReactDOM.render(<Dropdown items={dorpdownItems}></Dropdown>, div);
});