import ToggleButtons from "../../../components/controls/ToggleButtons"
import ReactDOM from "react-dom";

const statusButtons = [
  { label: 'Disabled', value: false },
  { label: 'Enabled', value: true }
];

jest.mock("react-redux", () => ({
  useSelector: jest.fn((fn) => fn()),
}));

it("render Toggle Button without crashing", () => {
  const div = document.createElement("div");
  ReactDOM.render(
    <ToggleButtons value={true} name="enabled" label="Status" buttons={statusButtons}></ToggleButtons>,
    div
  );
});