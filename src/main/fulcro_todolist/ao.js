

const Person = ({ id, onDelete }) => {
  return (
    <div onClick={() => onDelete(id)}>{name}</div>
  )
}
const List = () => {
  return (
    <div>
      {people.map(person => (
        <Person person={person} onDelete={(personId) => {
          aler
        }} />
      ))}
    </div>
  )
}

{
  "name": "Guilherme",
  "createdAt": "2013-01-01 01:01:01GT9",
  "amount": "10.00"
}
